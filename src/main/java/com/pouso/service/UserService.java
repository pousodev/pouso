package com.pouso.service;

import com.pouso.dto.UserListDTO;
import com.pouso.dto.UserListDTO.UserItem;
import com.pouso.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UsuarioRepository usuarioRepository;

    public UserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UserListDTO listPaged(int page, int size, String sortBy, String sortDir, String q) {
        int offset = page * size;
        List<UserItem> content = usuarioRepository.listarPaginado(offset, size, sortBy, sortDir, q);
        long total = usuarioRepository.contarTodos(q);
        return new UserListDTO(content, page, size, total);
    }
}
