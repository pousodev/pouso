package com.pouso.dto;

import java.util.List;

public class PetOwnerListDTO {

    private final List<OwnerItem> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PetOwnerListDTO(List<OwnerItem> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    }

    public List<OwnerItem> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isFirst() { return page <= 0; }
    public boolean isLast() { return page >= totalPages - 1; }

    public static class OwnerItem {
        private final String cpf;
        private final String nome;
        private final long petCount;
        private List<PetItem> pets;

        public OwnerItem(String cpf, String nome, long petCount) {
            this.cpf = cpf;
            this.nome = nome;
            this.petCount = petCount;
        }

        public String getCpf() { return cpf; }
        public String getNome() { return nome; }
        public long getPetCount() { return petCount; }
        public List<PetItem> getPets() { return pets; }
        public void setPets(List<PetItem> pets) { this.pets = pets; }
    }

    public static class PetItem {
        private final String nome;
        private final String tipoPetNome;
        private final String cpfDono;

        public PetItem(String nome, String tipoPetNome, String cpfDono) {
            this.nome = nome;
            this.tipoPetNome = tipoPetNome;
            this.cpfDono = cpfDono;
        }

        public String getNome() { return nome; }
        public String getTipoPetNome() { return tipoPetNome; }
        public String getCpfDono() { return cpfDono; }
    }
}
