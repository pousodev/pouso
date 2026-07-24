package com.pouso.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pouso.model.Pet;
import com.pouso.model.PetSolicitacao;
import com.pouso.dto.PetOwnerListDTO.OwnerItem;
import com.pouso.dto.PetOwnerListDTO.PetItem;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;

@Repository
public class PetRepository {

    private static final Set<String> ALLOWED_SORT = Set.of("nome", "data_registro", "pet_count");
    private static final Set<String> ALLOWED_DIR = Set.of("asc", "desc");

    private final JdbcTemplate jdbc;

    public PetRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void salvar(Pet pet) {
        String sql = """
                INSERT INTO pet (
                    nome, cpf_dono, bio, sexo, tipo_pet,
                    data_nasc, data_cadastro, porte, is_castrado
                ) VALUES (?, ?, ?, ?::sexo_enum, ?, ?, ?, ?::porte_enum, ?)
            """;

        jdbc.update(
            sql,
            pet.getNome(),
            pet.getCpfDono(),
            pet.getBio(),
            pet.getSexo(),
            pet.getTipoPet(),
            pet.getDataNasc(),
            pet.getDataCadastro(),
            pet.getPorte(),
            pet.getIsCastrado()
        );

    }

    public List<OwnerItem> listarProprietariosPaginado(int offset, int limit, String sortBy, String sortDir, String q) {
        String col = ALLOWED_SORT.contains(sortBy) ? sortBy : "nome";
        String dir = ALLOWED_DIR.contains(sortDir) ? sortDir.toUpperCase() : "ASC";

        String orderClause;
        switch (col) {
            case "data_registro":
                orderClause = "MAX(p.data_registro) " + dir;
                break;
            case "pet_count":
                orderClause = "COUNT(pet.nome) " + dir;
                break;
            default:
                orderClause = "p.nome " + dir;
        }

        boolean hasFilter = q != null && !q.isBlank();

        String sql = """
                SELECT p.cpf, p.nome, COUNT(pet.nome) AS pet_count
                FROM usuario u
                JOIN pessoa p ON p.cpf = u.cpf
                JOIN pet ON pet.cpf_dono = u.cpf
                %s
                GROUP BY p.cpf, p.nome
                ORDER BY %s
                LIMIT ? OFFSET ?
                """.formatted(
                hasFilter ? "WHERE LOWER(p.nome) LIKE LOWER(?) OR LOWER(pet.nome) LIKE LOWER(?)" : "",
                orderClause
        );

        Object[] params;
        if (hasFilter) {
            params = new Object[]{"%" + q + "%", "%" + q + "%", limit, offset};
        } else {
            params = new Object[]{limit, offset};
        }

        return jdbc.query(sql, (rs, rowNum) -> {
            return new OwnerItem(
                    rs.getString("cpf"),
                    rs.getString("nome"),
                    rs.getLong("pet_count")
            );
        }, params);
    }

    public long contarProprietarios(String q) {
        boolean hasFilter = q != null && !q.isBlank();

        String sql = """
                SELECT COUNT(DISTINCT u.cpf)
                FROM usuario u
                JOIN pessoa p ON p.cpf = u.cpf
                JOIN pet ON pet.cpf_dono = u.cpf
                %s
                """.formatted(
                hasFilter ? "WHERE LOWER(p.nome) LIKE LOWER(?) OR LOWER(pet.nome) LIKE LOWER(?)" : ""
        );

        Object[] params = hasFilter ? new Object[]{"%" + q + "%", "%" + q + "%"} : new Object[]{};
        Long result = jdbc.queryForObject(sql, Long.class, params);
        return result != null ? result : 0;
    }

    public List<PetItem> buscarPetsPorCpfs(List<String> cpfs) {
        if (cpfs == null || cpfs.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder sql = new StringBuilder("""
                SELECT pet.nome, tp.nome AS tipo_pet_nome, pet.cpf_dono
                FROM pet
                LEFT JOIN tipo_pet tp ON tp.id = pet.tipo_pet
                WHERE pet.cpf_dono IN (
                """);

        String placeholders = cpfs.stream().map(c -> "?").collect(java.util.stream.Collectors.joining(","));
        sql.append(placeholders);
        sql.append(") ORDER BY pet.nome");

        return jdbc.query(sql.toString(), (rs, rowNum) -> {
            return new PetItem(
                    rs.getString("nome"),
                    rs.getString("tipo_pet_nome"),
                    rs.getString("cpf_dono")
            );
        }, cpfs.toArray());
    }

    public List<PetSolicitacao> listarPendentes() {
        String sql = """
                SELECT pt.nome, pt.cpf_dono, dono.nome AS dono_nome,
                       especie.nome AS especie_nome, raca.nome AS raca_nome,
                       pt.sexo, pt.porte, pt.bio, pt.is_castrado, pt.is_permanente,
                       pt.data_nasc, pt.data_cadastro, pt.foto_pet
                FROM pet pt
                INNER JOIN pessoa dono ON dono.cpf = pt.cpf_dono
                INNER JOIN tipo_pet raca ON raca.id = pt.tipo_pet
                LEFT JOIN tipo_pet especie ON especie.id = raca.tipo_mae
                WHERE pt.status_aprovacao = 'PENDENTE'
                ORDER BY pt.data_cadastro ASC
            """;

        return jdbc.query(sql, (rs, rowNum) -> new PetSolicitacao(
            rs.getString("nome"),
            rs.getString("cpf_dono"),
            rs.getString("dono_nome"),
            rs.getString("especie_nome"),
            rs.getString("raca_nome"),
            rs.getString("sexo"),
            rs.getString("porte"),
            rs.getString("bio"),
            (Boolean) rs.getObject("is_castrado"),
            (Boolean) rs.getObject("is_permanente"),
            rs.getObject("data_nasc", LocalDate.class),
            rs.getObject("data_cadastro", LocalDate.class),
            "PENDENTE",
            null,
            rs.getString("foto_pet")
        ));
    }

    public List<PetSolicitacao> listarHistorico() {
        String sql = """
                SELECT pt.nome, pt.cpf_dono, dono.nome AS dono_nome,
                       especie.nome AS especie_nome, raca.nome AS raca_nome,
                       pt.sexo, pt.porte, pt.bio, pt.is_castrado, pt.is_permanente,
                       pt.data_nasc, pt.data_cadastro, pt.foto_pet,
                       pt.status_aprovacao, adm.nome AS admin_nome
                FROM pet pt
                INNER JOIN pessoa dono ON dono.cpf = pt.cpf_dono
                INNER JOIN tipo_pet raca ON raca.id = pt.tipo_pet
                LEFT JOIN tipo_pet especie ON especie.id = raca.tipo_mae
                LEFT JOIN pessoa adm ON adm.cpf = pt.adm_aprovou
                WHERE pt.status_aprovacao IN ('APROVADO', 'REJEITADO')
                ORDER BY pt.data_cadastro DESC
            """;

        return jdbc.query(sql, (rs, rowNum) -> new PetSolicitacao(
            rs.getString("nome"),
            rs.getString("cpf_dono"),
            rs.getString("dono_nome"),
            rs.getString("especie_nome"),
            rs.getString("raca_nome"),
            rs.getString("sexo"),
            rs.getString("porte"),
            rs.getString("bio"),
            (Boolean) rs.getObject("is_castrado"),
            (Boolean) rs.getObject("is_permanente"),
            rs.getObject("data_nasc", LocalDate.class),
            rs.getObject("data_cadastro", LocalDate.class),
            rs.getString("status_aprovacao"),
            rs.getString("admin_nome"),
            rs.getString("foto_pet")
        ));
    }

public List<PetSolicitacao> listarAprovadosPorDono(String cpfDono) {
    String sql = """
            SELECT pt.nome, pt.cpf_dono, dono.nome AS dono_nome,
                   especie.nome AS especie_nome, raca.nome AS raca_nome,
                   pt.sexo, pt.porte, pt.bio, pt.is_castrado, pt.is_permanente,
                   pt.data_nasc, pt.data_cadastro, pt.foto_pet,
                   pt.status_aprovacao, adm.nome AS admin_nome
            FROM pet pt
            INNER JOIN pessoa dono ON dono.cpf = pt.cpf_dono
            INNER JOIN tipo_pet raca ON raca.id = pt.tipo_pet
            LEFT JOIN tipo_pet especie ON especie.id = raca.tipo_mae
            LEFT JOIN pessoa adm ON adm.cpf = pt.adm_aprovou
            WHERE pt.status_aprovacao = 'APROVADO' AND pt.cpf_dono = ?
            ORDER BY pt.data_cadastro DESC
        """;

    return jdbc.query(sql, (rs, rowNum) -> new PetSolicitacao(
        rs.getString("nome"),
        rs.getString("cpf_dono"),
        rs.getString("dono_nome"),
        rs.getString("especie_nome"),
        rs.getString("raca_nome"),
        rs.getString("sexo"),
        rs.getString("porte"),
        rs.getString("bio"),
        (Boolean) rs.getObject("is_castrado"),
        (Boolean) rs.getObject("is_permanente"),
        rs.getObject("data_nasc", LocalDate.class),
        rs.getObject("data_cadastro", LocalDate.class),
        rs.getString("status_aprovacao"),
        rs.getString("admin_nome"),
        rs.getString("foto_pet")
    ), cpfDono);
}

    public void aprovar(String nomePet, String cpfDono, String cpfAdmin) {
        String sql = """
                UPDATE pet
                SET status_aprovacao = 'APROVADO', adm_aprovou = ?
                WHERE nome = ? AND cpf_dono = ?
            """;
        jdbc.update(sql, cpfAdmin, nomePet, cpfDono);
    }

    public void rejeitar(String nomePet, String cpfDono, String cpfAdmin) {
        String sql = """
                UPDATE pet
                SET status_aprovacao = 'REJEITADO', adm_aprovou = ?
                WHERE nome = ? AND cpf_dono = ?
            """;
        jdbc.update(sql, cpfAdmin, nomePet, cpfDono);
    }
}
