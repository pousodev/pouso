package com.pouso.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PersonRepository {

    private final JdbcTemplate jdbc;

    public PersonRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean emailExiste(String email) {
        String sql = """
                SELECT COUNT(*)
                FROM pessoa
                WHERE email = ?
                """;

        Integer count = jdbc.queryForObject(sql, Integer.class, email);

        return count != null && count > 0;
    }

    public boolean cpfExiste(String cpf) {
        String sql = """
                SELECT COUNT(*)
                FROM pessoa
                WHERE cpf = ?
                """;

        Integer count = jdbc.queryForObject(sql, Integer.class, cpf);

        return count != null && count > 0;
    }

    public void inserirPessoa(
        String cpf,
        String nome,
        String email,
        String senha
    ) {
        String sql = """
                INSERT INTO pessoa (cpf, nome, email, senha)
                VALUES (?, ?, ?, ?)
                """;

        jdbc.update(sql, cpf, nome, email, senha);
    }

    public void inserirUsuario(
        String cpf,
        String username
    ) {
        String sql = """
                INSERT INTO usuario (cpf, username)
                VALUES (?, ?)
                """;

        jdbc.update(sql, cpf, username);
    }
}