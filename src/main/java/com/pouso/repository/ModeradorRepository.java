package com.pouso.repository;

import com.pouso.model.Animal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ModeradorRepository {

    private final JdbcTemplate jdbc;

    public ModeradorRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Animal> getModerationHistory() {
        String sql = """
                SELECT p.nome, tp.nome as raca, p.data_nasc, p.porte, p.adm_aprovou
                , p.cpf_dono FROM pet p
                JOIN tipo_pet tp ON p.tipo_pet = tp.id
                WHERE p.adm_aprovou IS NOT NULL
            """;

        return jdbc.query(sql, (rs, rowNum) -> {
            LocalDate dataNasc = rs.getDate("data_nasc").toLocalDate();
            int years = Period.between(dataNasc, LocalDate.now()).getYears();
            int months = Period.between(dataNasc, LocalDate.now()).getMonths();
            String idade = (years > 0 ? years + (years == 1 ? " ano" : " anos") : months + (months == 1 ? " mês" : " meses"));

            String adminCpf = rs.getString("adm_aprovou");
            String status = "0000000000".equals(adminCpf) ? "RECUSADO" : "APROVADO";

            return new Animal(
                rs.getString("nome"),
                rs.getString("raca"),
                idade,
                rs.getString("porte"),
                "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTIwIiBoZWlnaHQ9IjEyMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTIwIiBoZWlnaHQ9IjEyMCIgc3R5bGU9ImZpbGw6I2VlZTtzdHJva2U6I2NjYyIgLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZG9taW5hbnQtYmFzZWxpbmU9Im1pZGRsZSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9InNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTRweCIgZmlsbD0iIzg4OCI+QW5pbWFsPC90ZXh0Pjwvc3ZnPg==", // Imagem placeholder
                status,
                rs.getString("cpf_dono")
            );
        });
    }

    public List<Animal> findPendingModerations() {
        String sql = """
            SELECT p.nome, tp.nome as raca, p.data_nasc, p.porte, p.cpf_dono
            FROM pet p
            JOIN tipo_pet tp ON p.tipo_pet = tp.id
            WHERE p.adm_aprovou IS NULL
            """;

        return jdbc.query(sql, (rs, rowNum) -> {
            LocalDate dataNasc = rs.getDate("data_nasc").toLocalDate();
            int years = Period.between(dataNasc, LocalDate.now()).getYears();
            int months = Period.between(dataNasc, LocalDate.now()).getMonths();
            String idade = (years > 0 ? years + (years == 1 ? " ano" : " anos") : months + (months == 1 ? " mês" : " meses"));

            return new Animal(
                rs.getString("nome"),
                rs.getString("raca"),
                idade,
                rs.getString("porte"),
                "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTIwIiBoZWlnaHQ9IjEyMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTIwIiBoZWlnaHQ9IjEyMCIgc3R5bGU9ImZpbGw6I2VlZTtzdHJva2U6I2NjYyIgLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZG9taW5hbnQtYmFzZWxpbmU9Im1pZGRsZSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9InNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMTRweCIgZmlsbD0iIzg4OCI+QW5pbWFsPC90ZXh0Pjwvc3ZnPg==", // Imagem placeholder
                "PENDENTE",
                rs.getString("cpf_dono")
            );
        });
    }

    public void updateModerationStatus(String petNome, String petDono, String adminCpf) {
        String sql = """
            UPDATE pet
            SET adm_aprovou = ?
            WHERE nome = ? AND cpf_dono = ?
            """;

        jdbc.update(sql, adminCpf, petNome, petDono);
    }

    public boolean isModerador(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM administrador WHERE cpf = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, cpf);
        return count != null && count > 0;
    }
}