package com.pouso.model;

public class Animal {
    private String nome;
    private String raca;
    private String idade;
    private String porte;
    private String imagem;
    private String status;
    private String cpfDono;

    public Animal(String nome, String raca, String idade, String porte, String imagem, String status, String cpfDono) {
        this.nome = nome;
        this.raca = raca;
        this.idade = idade;
        this.porte = porte;
        this.imagem = imagem;
        this.status = status;
        this.cpfDono = cpfDono;
    }

    public String getNome() {
        return nome;
    }

    public String getRaca() {
        return raca;
    }

    public String getIdade() {
        return idade;
    }

    public String getPorte() {
        return porte;
    }

    public String getImagem() {
        return imagem;
    }

    public String getStatus() {
        return status;
    }

    public String getCpfDono() {
        return cpfDono;
    }
}