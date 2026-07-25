package com.pouso.repository;

import com.pouso.model.AdoptionSummary;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AdoptionRepository {

    private final JdbcTemplate jdbc;

    public AdoptionRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<AdoptionSummary> listActiveAsAdopter(String cpf) {
        return list("""
                WHERE a.cpf_adotante = ?
                  AND a.status = 'EM_ANDAMENTO'
                ORDER BY a.data_inicio DESC
            """, cpf);
    }

    public List<AdoptionSummary> listCurrentAndRequestedAsAdopter(String cpf) {
        return list("""
                WHERE a.cpf_adotante = ?
                  AND a.status IN ('EM_ANDAMENTO', 'SOLICITADA')
                ORDER BY CASE WHEN a.status = 'EM_ANDAMENTO' THEN 0 ELSE 1 END,
                         a.data_solicitacao DESC, a.data_inicio DESC
            """, cpf);
    }

    public List<AdoptionSummary> listActiveAsDonor(String cpf) {
        return list("""
                WHERE a.pet_dono = ?
                  AND a.status = 'EM_ANDAMENTO'
                ORDER BY a.data_inicio DESC
            """, cpf);
    }

    public List<AdoptionSummary> listCurrentForOwnerPets(String cpf) {
        return list("""
                WHERE a.pet_dono = ?
                  AND a.status = 'EM_ANDAMENTO'
                ORDER BY a.data_inicio DESC
            """, cpf);
    }

    public List<AdoptionSummary> listHistory(String cpf) {
        return list("""
                WHERE (a.pet_dono = ? OR a.cpf_adotante = ?)
                  AND (a.status IN ('CONCLUIDA', 'CANCELADA')
                    OR a.data_fim IS NOT NULL
                    OR (a.status = 'EM_ANDAMENTO' AND a.is_permanente = true))
                ORDER BY COALESCE(a.data_fim, a.data_inicio) DESC
            """, cpf, cpf);
    }

    public List<AdoptionSummary> listRequestsForOwner(String cpf) {
        return list("""
                WHERE a.pet_dono = ?
                  AND a.status = 'SOLICITADA'
                ORDER BY a.data_solicitacao DESC, a.data_inicio DESC
            """, cpf);
    }

    public Optional<AdoptionSummary> findStatusForOwner(LocalDate startDate, String adopterCpf, String petName,
                                                        String petOwner, String ownerCpf) {
        return list("""
                WHERE a.data_inicio = ?
                  AND a.cpf_adotante = ?
                  AND a.pet_nome = ?
                  AND a.pet_dono = ?
                  AND a.pet_dono = ?
            """, startDate, adopterCpf, petName, petOwner, ownerCpf).stream().findFirst();
    }

    public Optional<AdoptionSummary> findStatusForParticipant(LocalDate startDate, String adopterCpf, String petName,
                                                             String petOwner, String cpf) {
        return list("""
                WHERE a.data_inicio = ?
                  AND a.cpf_adotante = ?
                  AND a.pet_nome = ?
                  AND a.pet_dono = ?
                  AND (a.pet_dono = ? OR a.cpf_adotante = ?)
                  AND a.status = 'EM_ANDAMENTO'
            """, startDate, adopterCpf, petName, petOwner, cpf, cpf).stream().findFirst();
    }

    @Transactional
    public String acceptRequest(LocalDate startDate, String adopterCpf, String petName, String ownerCpf) {
        String phone = jdbc.queryForObject("""
                SELECT telefone
                FROM usuario
                WHERE cpf = ?
            """, String.class, adopterCpf);

        int updated = jdbc.update("""
                UPDATE adocao
                SET status = 'EM_ANDAMENTO'
                WHERE data_inicio = ?
                  AND cpf_adotante = ?
                  AND pet_nome = ?
                  AND pet_dono = ?
                  AND status = 'SOLICITADA'
            """, startDate, adopterCpf, petName, ownerCpf);

        if (updated == 0) return null;

        jdbc.update("""
                INSERT INTO notificacao (pessoa_cpf, data, mensagem, is_lido)
                VALUES (?, CURRENT_TIMESTAMP, ?, false)
            """, ownerCpf, "Seu pet foi adotado :) Telefone do adotante: " + formatPhone(phone));

        return formatPhone(phone);
    }

    public boolean rejectRequest(LocalDate startDate, String adopterCpf, String petName, String ownerCpf) {
        int updated = jdbc.update("""
                UPDATE adocao
                SET status = 'CANCELADA'
                WHERE data_inicio = ?
                  AND cpf_adotante = ?
                  AND pet_nome = ?
                  AND pet_dono = ?
                  AND status = 'SOLICITADA'
            """, startDate, adopterCpf, petName, ownerCpf);

        return updated > 0;
    }

    private List<AdoptionSummary> list(String where, Object... params) {
        String sql = """
                SELECT a.data_inicio, a.cpf_adotante, adotante.nome AS adotante_nome,
                       adotante_usuario.username AS adotante_username,
                       adotante_usuario.telefone AS adotante_telefone,
                       adotante_usuario.foto_perfil AS adotante_foto,
                       a.pet_nome, a.pet_dono, dono.nome AS dono_nome,
                       a.data_fim, a.data_solicitacao, a.status::text AS status, a.is_permanente,
                       especie.nome AS especie_nome, raca.nome AS raca_nome,
                       p.sexo::text AS sexo, p.porte::text AS porte, p.data_nasc, p.foto_pet,
                       d.adocao_inicio IS NOT NULL AS tem_devolucao
                FROM adocao a
                INNER JOIN pet p ON p.nome = a.pet_nome AND p.cpf_dono = a.pet_dono
                INNER JOIN pessoa adotante ON adotante.cpf = a.cpf_adotante
                INNER JOIN usuario adotante_usuario ON adotante_usuario.cpf = a.cpf_adotante
                INNER JOIN pessoa dono ON dono.cpf = a.pet_dono
                INNER JOIN tipo_pet raca ON raca.id = p.tipo_pet
                LEFT JOIN tipo_pet especie ON especie.id = raca.tipo_mae
                LEFT JOIN devolucao d ON d.adocao_inicio = a.data_inicio
                    AND d.adocao_adotante = a.cpf_adotante
                    AND d.adocao_pet = a.pet_nome
                    AND d.adocao_dono = a.pet_dono
            """ + where;

        return jdbc.query(sql, (rs, rowNum) -> new AdoptionSummary(
            rs.getObject("data_inicio", LocalDate.class),
            rs.getString("cpf_adotante"),
            rs.getString("adotante_nome"),
            rs.getString("pet_nome"),
            rs.getString("pet_dono"),
            rs.getString("dono_nome"),
            rs.getString("adotante_username"),
            rs.getString("adotante_telefone"),
            rs.getString("adotante_foto"),
            rs.getObject("data_fim", LocalDate.class),
            rs.getObject("data_solicitacao", LocalDate.class),
            rs.getString("status"),
            (Boolean) rs.getObject("is_permanente"),
            rs.getString("especie_nome"),
            rs.getString("raca_nome"),
            rs.getString("sexo"),
            rs.getString("porte"),
            rs.getObject("data_nasc", LocalDate.class),
            rs.getString("foto_pet"),
            rs.getBoolean("tem_devolucao")
        ), params);
    }

    private String formatPhone(String phone) {
        if (phone == null || phone.isBlank()) return "telefone nao informado";
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() == 11) {
            return "(" + digits.substring(0, 2) + ") " + digits.substring(2, 7) + "-" + digits.substring(7);
        }
        return phone.trim();
    }
}
