package com.pouso.service;

import com.pouso.dto.UserListDTO;
import com.pouso.dto.UserListDTO.UserItem;
import com.pouso.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import com.pouso.model.Notificacao;
import com.pouso.repository.UserRepository;
import java.util.List;
import java.util.List;

@Service
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final UserRepository userRepository;

    public UserService(

    UsuarioRepository usuarioRepository,

    UserRepository userRepository

) {

    this.usuarioRepository = usuarioRepository;

    this.userRepository = userRepository;

}

public List<Notificacao> listarNotificacoes(String cpf) {
 List<Notificacao> notificacoes= userRepository.listarNotificacoes(cpf);
 return notificacoes;
}

    public UserListDTO listPaged(int page, int size, String sortBy, String sortDir, String q) {
        return listPaged(page, size, sortBy, sortDir, q, false);
    }

    public UserListDTO listPaged(int page, int size, String sortBy, String sortDir, String q, boolean onlyBanned) {
        int offset = page * size;
        List<UserItem> content = usuarioRepository.listarPaginado(offset, size, sortBy, sortDir, q, onlyBanned);
        long total = usuarioRepository.contarTodos(q, onlyBanned);
        return new UserListDTO(content, page, size, total);
    }
}
