package com.pouso.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserListDTO {

    private final List<UserItem> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public UserListDTO(List<UserItem> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    }

    public List<UserItem> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isFirst() { return page <= 0; }
    public boolean isLast() { return page >= totalPages - 1; }

    public static class UserItem {
        private final String cpf;
        private final String nome;
        private final String email;
        private final String username;
        private final String fotoPerfil;
        private final LocalDateTime dataRegistro;

        public UserItem(String cpf, String nome, String email, String username, String fotoPerfil, LocalDateTime dataRegistro) {
            this.cpf = cpf;
            this.nome = nome;
            this.email = email;
            this.username = username;
            this.fotoPerfil = fotoPerfil;
            this.dataRegistro = dataRegistro;
        }

        public String getCpf() { return cpf; }
        public String getNome() { return nome; }
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        public String getFotoPerfil() { return fotoPerfil; }
        public LocalDateTime getDataRegistro() { return dataRegistro; }
    }
}
