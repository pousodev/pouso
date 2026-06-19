package com.pouso.model;

import java.time.LocalDate;

public class Pet {

    private final String nome;
    private final String cpfDono;
    private final String bio;
    private final String sexo;
    private final int tipoPet;
    private final LocalDate dataNasc;
    private final LocalDate dataCadastro;
    private final String porte;
    private final boolean castrado;

    public Pet(
        String nome,
        String cpfDono,
        String bio,
        String sexo,
        int tipoPet,
        LocalDate dataNasc,
        LocalDate dataCadastro,
        String porte,
        boolean castrado
    ) {
        this.nome = nome;
        this.cpfDono = cpfDono;
        this.bio = bio;
        this.sexo = sexo;
        this.tipoPet = tipoPet;
        this.dataNasc = dataNasc;
        this.dataCadastro = dataCadastro;
        this.porte = porte;
        this.castrado = castrado;
    }

    public String getNome() {
        return nome;
    }

    public String getCpfDono() {
        return cpfDono;
    }

    public String getBio() {
        return bio;
    }

    public String getSexo() {
        return sexo;
    }

    public int getTipoPet() {
        return tipoPet;
    }

    public LocalDate getDataNasc() {
        return dataNasc;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public String getPorte() {
        return porte;
    }

    public boolean isCastrado() {
        return castrado;
    }
}
