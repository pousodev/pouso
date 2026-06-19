package com.pouso.model;
import java.time.LocalDate;

public class Pet {
    private String nome;
    private String cpfDono;
    
    private String bio;
    private String sexo;          
    private Integer tipoPet;      
    private LocalDate dataNasc;    // DATE mapeado para o java.time 
    private LocalDate dataCadastro;
    private String porte;          
    private Boolean isPermanente;
    private Boolean isCastrado;
    private String admAprovou;     

    public Pet(String nome, String cpfDono, String bio, String sexo, Integer tipoPet, 
               LocalDate dataNasc, LocalDate dataCadastro, String porte, 
               Boolean isPermanente, Boolean isCastrado, String admAprovou) {
        this.nome = nome;
        this.cpfDono = cpfDono;
        this.bio = bio;
        this.sexo = sexo;
        this.tipoPet = tipoPet;
        this.dataNasc = dataNasc;
        this.dataCadastro = dataCadastro;
        this.porte = porte;
        this.isPermanente = isPermanente;
        this.isCastrado = isCastrado;
        this.admAprovou = admAprovou;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpfDono() { return cpfDono; }
    public void setCpfDono(String cpfDono) { this.cpfDono = cpfDono; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public Integer getTipoPet() { return tipoPet; }
    public void setTipoPet(Integer tipoPet) { this.tipoPet = tipoPet; }

    public LocalDate getDataNasc() { return dataNasc; }
    public void setDataNasc(LocalDate dataNasc) { this.dataNasc = dataNasc; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getPorte() { return porte; }
    public void setPorte(String porte) { this.porte = porte; }

    public Boolean getIsPermanente() { return isPermanente; }
    public void setIsPermanente(Boolean isPermanente) { this.isPermanente = isPermanente; }

    public Boolean getIsCastrado() { return isCastrado; }
    public void setIsCastrado(Boolean isCastrado) { this.isCastrado = isCastrado; }

    public String getAdmAprovou() { return admAprovou; }
    public void setAdmAprovou(String admAprovou) { this.admAprovou = admAprovou; }
}