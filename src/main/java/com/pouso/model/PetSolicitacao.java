package com.pouso.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class PetSolicitacao {

    private String nome;
    private String cpfDono;
    private String donoNome;
    private String donoUsername;
    private String especieNome;
    private String racaNome;
    private String sexo;
    private String porte;
    private String bio;
    private Boolean isCastrado;
    private Boolean isPermanente;
    private LocalDate dataNasc;
    private LocalDate dataCadastro;
    private String statusAprovacao;
    private String adminNome;
    private String fotoPet;
    private boolean banned;

    public PetSolicitacao(String nome, String cpfDono, String donoNome, String especieNome,
                           String racaNome, String sexo, String porte, String bio,
                           Boolean isCastrado, Boolean isPermanente,
                           LocalDate dataNasc, LocalDate dataCadastro,
                            String statusAprovacao, String adminNome, String fotoPet) {
        this(nome, cpfDono, donoNome, especieNome, racaNome, sexo, porte, bio,
            isCastrado, isPermanente, dataNasc, dataCadastro, statusAprovacao,
            adminNome, fotoPet, false);
    }

    public PetSolicitacao(String nome, String cpfDono, String donoNome, String especieNome,
                           String racaNome, String sexo, String porte, String bio,
                           Boolean isCastrado, Boolean isPermanente,
                           LocalDate dataNasc, LocalDate dataCadastro,
                           String statusAprovacao, String adminNome, String fotoPet,
                           boolean banned) {
        this.nome = nome;
        this.cpfDono = cpfDono;
        this.donoNome = donoNome;
        this.especieNome = especieNome;
        this.racaNome = racaNome;
        this.sexo = sexo;
        this.porte = porte;
        this.bio = bio;
        this.isCastrado = isCastrado;
        this.isPermanente = isPermanente;
        this.dataNasc = dataNasc;
        this.dataCadastro = dataCadastro;
        this.statusAprovacao = statusAprovacao == null ? "PENDENTE" : statusAprovacao;
        this.adminNome = adminNome;
        this.fotoPet = fotoPet;
        this.banned = banned;
    }

    public String getNome() { return nome; }
    public String getCpfDono() { return cpfDono; }
    public String getDonoNome() { return donoNome; }
    public String getDonoUsername() { return donoUsername; }
    public void setDonoUsername(String donoUsername) { this.donoUsername = donoUsername; }
    public String getEspecieNome() { return especieNome; }
    public String getRacaNome() { return racaNome; }
    public String getSexo() { return sexo; }
    public String getPorte() { return porte; }
    public String getBio() { return bio; }
    public Boolean getIsCastrado() { return isCastrado; }
    public Boolean getIsPermanente() { return isPermanente; }
    public LocalDate getDataNasc() { return dataNasc; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public String getStatusAprovacao() { return statusAprovacao; }
    public String getAdminNome() { return adminNome; }
    public String getFotoPet() { return fotoPet; }
    public boolean isBanned() { return banned; }

    public boolean isAprovado() { return "APROVADO".equals(statusAprovacao); }
    public boolean isRejeitado() { return "REJEITADO".equals(statusAprovacao); }

    public String getTitulo() {
        return especieNome != null ? especieNome : racaNome;
    }

    // id seguro pra usar em atributos HTML (sem espaço/acento)
    public String getIdHtml() {
        return (nome + "-" + cpfDono).replaceAll("[^a-zA-Z0-9]", "");
    }

    public String getSexoDescricao() {
        if (sexo == null) return "-";
        return "M".equals(sexo) ? "Macho" : "Fêmea";
    }

    public String getIdadeDescricao() {
        if (dataNasc == null) return "Idade desconhecida";
        Period p = Period.between(dataNasc, LocalDate.now());
        if (p.getYears() > 0) {
            return p.getYears() + (p.getYears() == 1 ? " ano" : " anos");
        }
        int meses = p.getMonths();
        return meses + (meses == 1 ? " mês" : " meses");
    }

    public String getPorteDescricao() {
        if (porte == null) return "-";
        return switch (porte) {
            case "P" -> "Pequeno";
            case "M" -> "Médio";
            case "G" -> "Grande";
            default -> porte;
        };
    }

    public String getDataNascFormatada() {
        return dataNasc == null ? "-" : dataNasc.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getDataCadastroFormatada() {
        return dataCadastro == null ? "-" : dataCadastro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
