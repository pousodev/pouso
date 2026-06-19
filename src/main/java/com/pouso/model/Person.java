package com.pouso.model;

public class Person {

    private String cpf;
    //private String nome;
  //  private String email;
    private String password;

    public Person(String cpf, String password) {
        this.cpf = cpf;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getCPF() {
        return cpf;
    }
}
