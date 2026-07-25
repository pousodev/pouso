package com.pouso.repository;

import com.pouso.model.Person;
import java.util.List;
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
        String username,
        String telefone,
        String genero
    ) {
        String sql = """
                INSERT INTO usuario (cpf, username, telefone, genero)
                VALUES (?, ?, ?, ?::genero_enum)
                """;

        jdbc.update(sql, cpf, username, telefone, genero);
    }

    public void inserirEndereco(
        String cpf,
        String cep,
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf
    ) {
        String sql = """
                INSERT INTO endereco (
                    usuario_cpf,
                    cep,
                    rua,
                    numero,
                    complemento,
                    bairro,
                    cidade,
                    uf
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbc.update(sql, cpf, cep, rua, numero, complemento, bairro, cidade, uf);
    }

    public Person buscarPorEmail(String email) {
        String sql = """
                SELECT cpf, nome, email, senha
                FROM pessoa
                WHERE email = ?
                """;

        List<Person> pessoas = jdbc.query(
            sql,
            (rs, rowNum) ->
                new Person(
                    rs.getString("cpf"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("senha")
                ),
            email
        );

        if (pessoas.isEmpty()) {
            return null;
        }

        return pessoas.get(0);
    }

    public boolean isUsuarioBanido(String cpf) {
        String sql = "SELECT is_banned FROM usuario WHERE cpf = ?";
        List<Boolean> resultados = jdbc.query(sql, (rs, rowNum) -> rs.getBoolean("is_banned"), cpf);
        return !resultados.isEmpty() && Boolean.TRUE.equals(resultados.get(0));
    }
}
