package com.pouso.model;

public class SaudePet {

    private final String petNome;
    private final String petDono;
    private final boolean usaMedicamento;
    private final String descMedicamento;
    private final boolean condicaoEspecial;
    private final String descCuidados;

    public SaudePet(
        String petNome,
        String petDono,
        boolean usaMedicamento,
        String descMedicamento,
        boolean condicaoEspecial,
        String descCuidados
    ) {
        this.petNome = petNome;
        this.petDono = petDono;
        this.usaMedicamento = usaMedicamento;
        this.descMedicamento = descMedicamento;
        this.condicaoEspecial = condicaoEspecial;
        this.descCuidados = descCuidados;
    }

    public String getPetNome() {
        return petNome;
    }

    public String getPetDono() {
        return petDono;
    }

    public boolean isUsaMedicamento() {
        return usaMedicamento;
    }

    public String getDescMedicamento() {
        return descMedicamento;
    }

    public boolean isCondicaoEspecial() {
        return condicaoEspecial;
    }

    public String getDescCuidados() {
        return descCuidados;
    }
}
