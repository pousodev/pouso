package com.pouso.model;

public class Person {

    private String cpf;
    private String nome;
    private String email;
    private String password;

    public Person(String cpf, String nome, String email, String password) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    public String getCPF() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}