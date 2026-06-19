package com.pouso.repository;

import com.pouso.model.SaudePet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SaudePetRepository {

    private final JdbcTemplate jdbc;

    public SaudePetRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void salvar(SaudePet saudePet) {
        String sql = """
                INSERT INTO saude_pet (
                    pet_nome, pet_dono, usa_medicamento,
                    desc_medicamento, condicao_especial, desc_cuidados
                ) VALUES (?, ?, ?, ?, ?, ?)
            """;

        jdbc.update(
            sql,
            saudePet.getPetNome(),
            saudePet.getPetDono(),
            saudePet.isUsaMedicamento(),
            saudePet.getDescMedicamento(),
            saudePet.isCondicaoEspecial(),
            saudePet.getDescCuidados()
        );
    }
}
