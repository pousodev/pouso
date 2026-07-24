package com.pouso.repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TipoPetRepository {

    private final JdbcTemplate jdbc;

    public TipoPetRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public int buscarOuCriarPorNome(String nome) {
        String selectSql = "SELECT id FROM tipo_pet WHERE LOWER(nome) = LOWER(?)";

        List<Integer> ids = jdbc.query(
            selectSql,
            (rs, rowNum) -> rs.getInt("id"),
            nome
        );

        if (!ids.isEmpty()) {
            return ids.get(0);
        }

        String insertSql = "INSERT INTO tipo_pet (nome) VALUES (?) RETURNING id";
        return jdbc.queryForObject(insertSql, Integer.class, nome);
    }
}
