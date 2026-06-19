package com.pouso.repository;

import com.pouso.model.Pet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PetRepository {

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
            pet.isCastrado()
        );
    }
}
