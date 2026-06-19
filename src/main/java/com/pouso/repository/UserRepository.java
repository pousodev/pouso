package com.pouso.repository;
import com.pouso.model.User;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) { //
        this.jdbc = jdbc;
    }

    public User buscarPorCpf(String cpf) {
        String sql = """
                SELECT p.cpf, p.nome, p.email, p.senha, 
                       u.username, u.bio, u.genero, u.telefone, u.foto_perfil
                FROM pessoa p
                INNER JOIN usuario u ON p.cpf = u.cpf
                WHERE p.cpf = ?
            """;

        List<User> users = jdbc.query(
            sql,
            (rs, rowNum) -> { //trocar o map para criar um objeto User
                User u = new User(
                    rs.getString("cpf"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("senha"),
                    rs.getString("username"),
                    rs.getString("bio"),
                    rs.getString("genero"),
                    rs.getString("telefone"),
                    rs.getString("foto_perfil")
                );
                return u;
            },
            cpf
        );

        if (users.isEmpty()) {
            return null;
        }

        return users.get(0); //tirar lista
    }

    public void atualizar(User user) {
   
        String sqlPessoa = """
                UPDATE pessoa 
                SET nome = ?, email = ? 
                WHERE cpf = ?
            """;
        
        jdbc.update(sqlPessoa, user.getNome(), user.getEmail(), user.getCpf());

        // o cast '?::genero_enum' garante que o banco reconheça a String como o enum do banco
        String sqlUsuario = """
                UPDATE usuario 
                SET username = ?, bio = ?, genero = ?::genero_enum, telefone = ?, foto_perfil = ? 
                WHERE cpf = ?
            """;

        jdbc.update(
            sqlUsuario, 
            user.getUsername(), 
            user.getBio(), 
            user.getGenero(), 
            user.getTelefone(), 
            user.getFotoPerfil(), 
            user.getCpf()
        );
    }
}