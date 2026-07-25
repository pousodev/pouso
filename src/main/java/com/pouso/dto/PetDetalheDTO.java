package com.pouso.dto;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class PetDetalheDTO {

    private final String nome;
    private final String cpfDono;
    private final String donoNome;
    private final String donoUsername;
    private final String especieNome;
    private final String racaNome;
    private final String sexo;
    private final String porte;
    private final String bio;
    private final Boolean isCastrado;
    private final Boolean isPermanente;
    private final LocalDate dataNasc;
    private final String fotoPet;
    private final String statusAprovacao;
    private final Boolean usaMedicamento;
    private final Boolean condicaoEspecial;
    private final String cidade;
    private final String bairro;
    private final String adocaoAtualStatus;

    public PetDetalheDTO(String nome, String cpfDono, String donoNome, String donoUsername,
                          String especieNome, String racaNome, String sexo, String porte,
                          String bio, Boolean isCastrado, Boolean isPermanente,
                          LocalDate dataNasc, String fotoPet, String statusAprovacao,
                          Boolean usaMedicamento, Boolean condicaoEspecial,
                          String cidade, String bairro, String adocaoAtualStatus) {
        this.nome = nome;
        this.cpfDono = cpfDono;
        this.donoNome = donoNome;
        this.donoUsername = donoUsername;
        this.especieNome = especieNome;
        this.racaNome = racaNome;
        this.sexo = sexo;
        this.porte = porte;
        this.bio = bio;
        this.isCastrado = isCastrado;
        this.isPermanente = isPermanente;
        this.dataNasc = dataNasc;
        this.fotoPet = fotoPet;
        this.statusAprovacao = statusAprovacao;
        this.usaMedicamento = usaMedicamento;
        this.condicaoEspecial = condicaoEspecial;
        this.cidade = cidade;
        this.bairro = bairro;
        this.adocaoAtualStatus = adocaoAtualStatus;
    }

    public String getNome() { return nome; }
    public String getCpfDono() { return cpfDono; }
    public String getDonoNome() { return donoNome; }
    public String getDonoUsername() { return donoUsername; }
    public String getEspecieNome() { return especieNome; }
    public String getRacaNome() { return racaNome; }
    public String getSexo() { return sexo; }
    public String getPorte() { return porte; }
    public String getBio() { return bio; }
    public Boolean getIsCastrado() { return isCastrado; }
    public Boolean getIsPermanente() { return isPermanente; }
    public LocalDate getDataNasc() { return dataNasc; }
    public String getFotoPet() { return fotoPet; }
    public String getStatusAprovacao() { return statusAprovacao; }
    public Boolean getUsaMedicamento() { return usaMedicamento; }
    public Boolean getCondicaoEspecial() { return condicaoEspecial; }
    public String getCidade() { return cidade; }
    public String getBairro() { return bairro; }

    public boolean isAprovado() { return "APROVADO".equals(statusAprovacao); }
    public boolean isPendente() { return "PENDENTE".equals(statusAprovacao); }
    public boolean isRejeitado() { return "REJEITADO".equals(statusAprovacao); }
    public boolean isDisponivel() { return isAprovado() && adocaoAtualStatus == null; }

    public String getTitulo() {
        return especieNome != null ? especieNome : racaNome;
    }

    public String getSexoDescricao() {
        if (sexo == null) return "-";
        return "M".equals(sexo) ? "Macho" : "Fêmea";
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

    public String getIdadeDescricao() {
        if (dataNasc == null) return "Idade desconhecida";
        Period p = Period.between(dataNasc, LocalDate.now());
        if (p.getYears() > 0) {
            return p.getYears() + (p.getYears() == 1 ? " ano" : " anos");
        }
        int meses = p.getMonths();
        return meses + (meses == 1 ? " mês" : " meses");
    }

    public String getDataNascFormatada() {
        return dataNasc == null ? "-" : dataNasc.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getLocalizacao() {
        if (cidade == null || cidade.isBlank()) return null;
        return bairro != null && !bairro.isBlank() ? bairro + " - " + cidade : cidade;
    }

    public String getStatusExibicao() {
        if (!isAprovado()) {
            return isPendente() ? "Aguardando moderação" : "Não aprovado";
        }
        if ("SOLICITADA".equals(adocaoAtualStatus)) {
            return "Solicitação pendente";
        }
        if ("EM_ANDAMENTO".equals(adocaoAtualStatus)) {
            return "Em processo de adoção";
        }
        return "Disponível";
    }
}
