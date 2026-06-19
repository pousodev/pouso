package com.pouso.model;

public class User {
    private String cpf;     
    private String nome;
    private String email;
    private String senha;

 
    private String username;
    private String bio;
    private String genero;    
    private String telefone;
    private String fotoPerfil;

    public User(String cpf, String nome, String email, String senha, String username, String bio, String genero, String telefone, String fotoPerfil) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.username = username;
        this.bio = bio;
        this.genero = genero;
        this.telefone = telefone;
        this.fotoPerfil = fotoPerfil;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

}

