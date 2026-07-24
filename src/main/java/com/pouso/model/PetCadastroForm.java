package com.pouso.model;

import java.time.LocalDate;

public class PetCadastroForm {

    private String nome;
    private String especie;
    private LocalDate dataNascimento;
    private String sexo;
    private String porte;
    private boolean castrado;
    private boolean usaMedicamento;
    private String medicamentos;
    private String condicoesEspeciais;
    private String bio;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public boolean isCastrado() {
        return castrado;
    }

    public void setCastrado(boolean castrado) {
        this.castrado = castrado;
    }

    public boolean isUsaMedicamento() {
        return usaMedicamento;
    }

    public void setUsaMedicamento(boolean usaMedicamento) {
        this.usaMedicamento = usaMedicamento;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
    }

    public String getCondicoesEspeciais() {
        return condicoesEspeciais;
    }

    public void setCondicoesEspeciais(String condicoesEspeciais) {
        this.condicoesEspeciais = condicoesEspeciais;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
