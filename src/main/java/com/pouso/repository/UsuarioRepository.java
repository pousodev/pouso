package com.pouso.repository;

import com.pouso.dto.UserListDTO.UserItem;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {

    private static final Set<String> ALLOWED_SORT = Set.of("nome", "data_registro");
    private static final Set<String> ALLOWED_DIR = Set.of("asc", "desc");

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<UserItem> listarPaginado(int offset, int limit, String sortBy, String sortDir, String q) {
        String col = ALLOWED_SORT.contains(sortBy) ? sortBy : "nome";
        String dir = ALLOWED_DIR.contains(sortDir) ? sortDir.toUpperCase() : "ASC";

        boolean hasFilter = q != null && !q.isBlank();

        String sql = """
                SELECT p.cpf, p.nome, p.email, u.username, u.foto_perfil, p.data_registro
                FROM pessoa p
                JOIN usuario u ON u.cpf = p.cpf
                %s
                ORDER BY %s %s
                LIMIT ? OFFSET ?
            """.formatted(hasFilter ? "WHERE LOWER(u.username) LIKE LOWER(?)" : "", col, dir);

        Object[] params;
        if (hasFilter) {
            params = new Object[]{"%" + q + "%", limit, offset};
        } else {
            params = new Object[]{limit, offset};
        }

        return jdbc.query(sql, (rs, rowNum) -> {
            Timestamp ts = rs.getTimestamp("data_registro");
            LocalDateTime dataRegistro = ts != null ? ts.toLocalDateTime() : null;
            return new UserItem(
                rs.getString("cpf"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("foto_perfil"),
                dataRegistro
            );
        }, params);
    }

    public long contarTodos(String q) {
        boolean hasFilter = q != null && !q.isBlank();

        String sql = """
                SELECT COUNT(*)
                FROM pessoa p
                JOIN usuario u ON u.cpf = p.cpf
                %s
            """.formatted(hasFilter ? "WHERE LOWER(u.username) LIKE LOWER(?)" : "");

        Object[] params = hasFilter ? new Object[]{"%" + q + "%"} : new Object[]{};
        Long result = jdbc.queryForObject(sql, Long.class, params);
        return result != null ? result : 0;
    }

    public String buscarNivelAdmin(String cpf) {
        String sql = "SELECT nivel FROM administrador WHERE cpf = ?";
        List<String> resultados = jdbc.query(sql, (rs, rowNum) -> rs.getString("nivel"), cpf);
        return resultados.isEmpty() ? null : resultados.get(0);
    }
}
