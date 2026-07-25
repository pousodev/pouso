package com.pouso.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pouso.model.Pet;
import com.pouso.model.PetSolicitacao;
import com.pouso.dto.PetDetalheDTO;
import com.pouso.dto.PetOwnerListDTO.OwnerItem;
import com.pouso.dto.PetOwnerListDTO.PetItem;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
                SELECT p.cpf, p.nome, u.username, COUNT(pet.nome) AS pet_count
                FROM usuario u
                JOIN pessoa p ON p.cpf = u.cpf
                JOIN pet ON pet.cpf_dono = u.cpf
                %s
                GROUP BY p.cpf, p.nome, u.username
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
                    rs.getString("username"),
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
                SELECT pet.nome, tp.nome AS tipo_pet_nome, pet.cpf_dono,
                       pet.status_aprovacao, pet.is_banned
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
                    rs.getString("cpf_dono"),
                    rs.getString("status_aprovacao"),
                    rs.getBoolean("is_banned")
            );
        }, cpfs.toArray());
    }

    public List<OwnerItem> listarBanidosPorDono() {
        String sql = """
                SELECT p.cpf, p.nome, u.username, COUNT(pet.nome) AS pet_count
                FROM usuario u
                JOIN pessoa p ON p.cpf = u.cpf
                JOIN pet ON pet.cpf_dono = u.cpf
                WHERE pet.is_banned = TRUE
                GROUP BY p.cpf, p.nome, u.username
                ORDER BY p.nome ASC
                """;

        List<OwnerItem> owners = jdbc.query(sql, (rs, rowNum) -> new OwnerItem(
                rs.getString("cpf"),
                rs.getString("nome"),
                rs.getString("username"),
                rs.getLong("pet_count")
        ));

        if (owners.isEmpty()) {
            return owners;
        }

        List<String> cpfs = owners.stream().map(OwnerItem::getCpf).toList();
        List<PetItem> pets = buscarPetsBanidosPorCpfs(cpfs);
        java.util.Map<String, List<PetItem>> petsByCpf = pets.stream()
                .collect(java.util.stream.Collectors.groupingBy(PetItem::getCpfDono));

        for (OwnerItem owner : owners) {
            owner.setPets(petsByCpf.getOrDefault(owner.getCpf(), List.of()));
        }

        return owners;
    }

    private List<PetItem> buscarPetsBanidosPorCpfs(List<String> cpfs) {
        String placeholders = cpfs.stream().map(c -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = """
                SELECT pet.nome, tp.nome AS tipo_pet_nome, pet.cpf_dono,
                       pet.status_aprovacao, pet.is_banned
                FROM pet
                LEFT JOIN tipo_pet tp ON tp.id = pet.tipo_pet
                WHERE pet.is_banned = TRUE
                  AND pet.cpf_dono IN (%s)
                ORDER BY pet.nome ASC
                """.formatted(placeholders);

        return jdbc.query(sql, (rs, rowNum) -> new PetItem(
                rs.getString("nome"),
                rs.getString("tipo_pet_nome"),
                rs.getString("cpf_dono"),
                rs.getString("status_aprovacao"),
                rs.getBoolean("is_banned")
        ), cpfs.toArray());
    }

    public List<PetSolicitacao> listByOwner(String cpf) {
        String sql = """
                SELECT pt.nome, pt.cpf_dono, dono.nome AS dono_nome, dono_u.username AS dono_username,
                       especie.nome AS especie_nome, raca.nome AS raca_nome,
                       pt.sexo, pt.porte, pt.bio, pt.is_castrado, pt.is_permanente,
                       pt.data_nasc, pt.data_cadastro, pt.foto_pet,
                       pt.status_aprovacao, adm.nome AS admin_nome, pt.is_banned
                FROM pet pt
                INNER JOIN pessoa dono ON dono.cpf = pt.cpf_dono
                INNER JOIN usuario dono_u ON dono_u.cpf = pt.cpf_dono
                INNER JOIN tipo_pet raca ON raca.id = pt.tipo_pet
                LEFT JOIN tipo_pet especie ON especie.id = raca.tipo_mae
                LEFT JOIN pessoa adm ON adm.cpf = pt.adm_aprovou
                WHERE pt.cpf_dono = ?
                ORDER BY pt.data_cadastro DESC, pt.nome ASC
            """;

        return jdbc.query(sql, (rs, rowNum) -> {
            PetSolicitacao pet = new PetSolicitacao(
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
            rs.getString("foto_pet"),
            rs.getBoolean("is_banned")
            );
            pet.setDonoUsername(rs.getString("dono_username"));
            return pet;
        }, cpf);
    }

    public PetSolicitacao findByOwnerAndName(String cpfDono, String nome) {
        String sql = """
                SELECT pt.nome, pt.cpf_dono, dono.nome AS dono_nome, dono_u.username AS dono_username,
                       especie.nome AS especie_nome, raca.nome AS raca_nome,
                       pt.sexo, pt.porte, pt.bio, pt.is_castrado, pt.is_permanente,
                       pt.data_nasc, pt.data_cadastro, pt.foto_pet,
                       pt.status_aprovacao, adm.nome AS admin_nome, pt.is_banned
                FROM pet pt
                INNER JOIN pessoa dono ON dono.cpf = pt.cpf_dono
                INNER JOIN usuario dono_u ON dono_u.cpf = pt.cpf_dono
                INNER JOIN tipo_pet raca ON raca.id = pt.tipo_pet
                LEFT JOIN tipo_pet especie ON especie.id = raca.tipo_mae
                LEFT JOIN pessoa adm ON adm.cpf = pt.adm_aprovou
                WHERE pt.cpf_dono = ? AND pt.nome = ?
            """;

        List<PetSolicitacao> pets = jdbc.query(sql, (rs, rowNum) -> {
            PetSolicitacao pet = new PetSolicitacao(
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
                rs.getString("foto_pet"),
                rs.getBoolean("is_banned")
            );
            pet.setDonoUsername(rs.getString("dono_username"));
            return pet;
        }, cpfDono, nome);
        return pets.isEmpty() ? null : pets.get(0);
    }

    public PetSolicitacao findByOwnerUsernameAndName(String username, String nome) {
        String sql = """
                SELECT pt.nome, pt.cpf_dono, dono.nome AS dono_nome, u.username AS dono_username,
                       especie.nome AS especie_nome, raca.nome AS raca_nome,
                       pt.sexo, pt.porte, pt.bio, pt.is_castrado, pt.is_permanente,
                       pt.data_nasc, pt.data_cadastro, pt.foto_pet,
                       pt.status_aprovacao, adm.nome AS admin_nome, pt.is_banned
                FROM pet pt
                INNER JOIN usuario u ON u.cpf = pt.cpf_dono
                INNER JOIN pessoa dono ON dono.cpf = pt.cpf_dono
                INNER JOIN tipo_pet raca ON raca.id = pt.tipo_pet
                LEFT JOIN tipo_pet especie ON especie.id = raca.tipo_mae
                LEFT JOIN pessoa adm ON adm.cpf = pt.adm_aprovou
                WHERE u.username = ? AND pt.nome = ?
            """;

        List<PetSolicitacao> pets = jdbc.query(sql, (rs, rowNum) -> {
            PetSolicitacao pet = new PetSolicitacao(
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
                rs.getString("foto_pet"),
                rs.getBoolean("is_banned")
            );
            pet.setDonoUsername(rs.getString("dono_username"));
            return pet;
        }, username, nome);
        return pets.isEmpty() ? null : pets.get(0);
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

    public List<PetSolicitacao> listRejectedByOwner(String cpf) {
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
                WHERE pt.cpf_dono = ?
                  AND pt.status_aprovacao = 'REJEITADO'
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
        ), cpf);
    }
public List<PetSolicitacao> listarAprovadosPorDono(String cpfDono) {
    String sql = """
            SELECT pt.nome, pt.cpf_dono, dono.nome AS dono_nome, dono_u.username AS dono_username,
                   especie.nome AS especie_nome, raca.nome AS raca_nome,
                   pt.sexo, pt.porte, pt.bio, pt.is_castrado, pt.is_permanente,
                   pt.data_nasc, pt.data_cadastro, pt.foto_pet,
                   pt.status_aprovacao, adm.nome AS admin_nome
            FROM pet pt
            INNER JOIN pessoa dono ON dono.cpf = pt.cpf_dono
            INNER JOIN usuario dono_u ON dono_u.cpf = pt.cpf_dono
            INNER JOIN tipo_pet raca ON raca.id = pt.tipo_pet
            LEFT JOIN tipo_pet especie ON especie.id = raca.tipo_mae
            LEFT JOIN pessoa adm ON adm.cpf = pt.adm_aprovou
            WHERE pt.status_aprovacao = 'APROVADO' AND pt.cpf_dono = ? AND pt.is_banned = FALSE
            ORDER BY pt.data_cadastro DESC
        """;

    return jdbc.query(sql, (rs, rowNum) -> {
        PetSolicitacao pet = new PetSolicitacao(
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
        );
        pet.setDonoUsername(rs.getString("dono_username"));
        return pet;
    }, cpfDono);
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

    public void setBanido(String nomePet, String cpfDono, boolean banned) {
        jdbc.update("UPDATE pet SET is_banned = ? WHERE nome = ? AND cpf_dono = ?", banned, nomePet, cpfDono);
    }

    public void setBanidoByOwnerUsername(String nomePet, String username, boolean banned) {
        jdbc.update("""
                UPDATE pet
                SET is_banned = ?
                WHERE nome = ?
                  AND cpf_dono = (SELECT cpf FROM usuario WHERE username = ?)
            """, banned, nomePet, username);
    }




       public List<Pet> buscarPets(
            Boolean isPermanente,
            String cidade,
            String bairro,
            int pagina,
            int tamanhoPagina
    ) {
        int offset = (pagina - 1) * tamanhoPagina;

        String sql = """
            SELECT
                p.nome,
                p.cpf_dono,
                p.bio,
                p.sexo,
                p.tipo_pet,
                p.data_nasc,
                p.data_cadastro,
                p.porte,
                p.is_permanente,
                p.is_castrado,
                p.adm_aprovou,
                p.foto_pet,
                u.username AS dono_username

            FROM pet p

            INNER JOIN endereco e
                ON p.cpf_dono = e.usuario_cpf

            INNER JOIN usuario u
                ON u.cpf = p.cpf_dono

            WHERE p.status_aprovacao = 'APROVADO' AND p.is_banned = FALSE

         AND (?::boolean IS NULL OR p.is_permanente = ?)

AND (?::varchar IS NULL OR e.cidade = ?)

AND (?::varchar IS NULL OR e.bairro = ?)

            ORDER BY p.data_cadastro DESC

            LIMIT ? OFFSET ?
            """;

        return jdbc.query(

                sql,

                (rs, rowNum) -> {
                    Pet pet = new Pet(

                        rs.getString("nome"),
                        rs.getString("cpf_dono"),
                        rs.getString("bio"),
                        rs.getString("sexo"),
                        rs.getInt("tipo_pet"),
                        rs.getDate("data_nasc") == null
                                ? null
                                : rs.getDate("data_nasc").toLocalDate(),
                        rs.getDate("data_cadastro") == null
                                ? null
                                : rs.getDate("data_cadastro").toLocalDate(),
                        rs.getString("porte"),
                        rs.getBoolean("is_permanente"),
                        rs.getBoolean("is_castrado"),
                        rs.getString("adm_aprovou"),
                        rs.getString("foto_pet")
                    );
                    pet.setDonoUsername(rs.getString("dono_username"));
                    return pet;
                },

                isPermanente,
                isPermanente,

                cidade,
                cidade,

                bairro,
                bairro,

                tamanhoPagina,
                offset
        );
    }

   public int contarPets(
        Boolean isPermanente,
        String cidade,
        String bairro
) {

    String sql = """
        SELECT COUNT(*)

        FROM pet p

        INNER JOIN endereco e
            ON p.cpf_dono = e.usuario_cpf

        WHERE p.status_aprovacao = 'APROVADO' AND p.is_banned = FALSE

        AND (?::boolean IS NULL OR p.is_permanente = ?)

        AND (?::varchar IS NULL OR e.cidade = ?)

        AND (?::varchar IS NULL OR e.bairro = ?)
        """;


    Integer total = jdbc.queryForObject(
            sql,
            Integer.class,

            isPermanente,
            isPermanente,

            cidade,
            cidade,

            bairro,
            bairro
    );


    return total == null ? 0 : total;
}



    public Optional<PetDetalheDTO> buscarDetalhe(String cpfDono, String nome) {
        String sql = """
                SELECT pt.nome, pt.cpf_dono, dono.nome AS dono_nome, dono_u.username AS dono_username,
                       especie.nome AS especie_nome, raca.nome AS raca_nome,
                       pt.sexo, pt.porte, pt.bio, pt.is_castrado, pt.is_permanente,
                       pt.data_nasc, pt.foto_pet, pt.status_aprovacao,
                       sp.usa_medicamento, sp.condicao_especial,
                       ende.cidade, ende.bairro,
                       (
                           SELECT a.status::text FROM adocao a
                           WHERE a.pet_nome = pt.nome AND a.pet_dono = pt.cpf_dono
                              AND a.status IN ('SOLICITADA', 'EM_ANDAMENTO')
                           LIMIT 1
                       ) AS adocao_atual_status
                FROM pet pt
                INNER JOIN pessoa dono ON dono.cpf = pt.cpf_dono
                INNER JOIN usuario dono_u ON dono_u.cpf = pt.cpf_dono
                INNER JOIN tipo_pet raca ON raca.id = pt.tipo_pet
                LEFT JOIN tipo_pet especie ON especie.id = raca.tipo_mae
                LEFT JOIN saude_pet sp ON sp.pet_nome = pt.nome AND sp.pet_dono = pt.cpf_dono
                LEFT JOIN endereco ende ON ende.usuario_cpf = pt.cpf_dono
                WHERE pt.cpf_dono = ? AND pt.nome = ?
            """;

        List<PetDetalheDTO> resultado = jdbc.query(sql, (rs, rowNum) -> new PetDetalheDTO(
            rs.getString("nome"),
            rs.getString("cpf_dono"),
            rs.getString("dono_nome"),
            rs.getString("dono_username"),
            rs.getString("especie_nome"),
            rs.getString("raca_nome"),
            rs.getString("sexo"),
            rs.getString("porte"),
            rs.getString("bio"),
            (Boolean) rs.getObject("is_castrado"),
            (Boolean) rs.getObject("is_permanente"),
            rs.getObject("data_nasc", LocalDate.class),
            rs.getString("foto_pet"),
            rs.getString("status_aprovacao"),
            (Boolean) rs.getObject("usa_medicamento"),
            (Boolean) rs.getObject("condicao_especial"),
            rs.getString("cidade"),
            rs.getString("bairro"),
            rs.getString("adocao_atual_status")
        ), cpfDono, nome);

        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    public List<String> listarCidades() {

        String sql = """
            SELECT DISTINCT cidade
            FROM endereco
            ORDER BY cidade
            """;

        return jdbc.query(
                sql,
                (rs, rowNum) -> rs.getString("cidade")
        );
    }

    public List<String> listarBairros() {

        String sql = """
            SELECT DISTINCT bairro
            FROM endereco
            ORDER BY bairro
            """;

        return jdbc.query(
                sql,
                (rs, rowNum) -> rs.getString("bairro")
        );
    }
}
