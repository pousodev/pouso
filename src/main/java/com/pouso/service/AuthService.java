package com.pouso.service;

import com.pouso.dto.CadastroRequest;
import com.pouso.model.Person;
import com.pouso.repository.PersonRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PersonRepository personRepository;

    public AuthService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person cadastrar(CadastroRequest request) {
        validarCadastro(request);

        if (personRepository.cpfExiste(request.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        if (personRepository.emailExiste(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        personRepository.inserirPessoa(
            request.getCpf(),
            request.getNome(),
            request.getEmail(),
            request.getPassword()
        );

        personRepository.inserirUsuario(
            request.getCpf(),
            request.getUsername()
        );

        return new Person(
            request.getCpf(),
            request.getNome(),
            request.getEmail(),
            request.getPassword()
        );
    }

    private void validarCadastro(CadastroRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do cadastro são obrigatórios");
        }

        if (isBlank(request.getCpf()) || request.getCpf().length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        }

        if (isBlank(request.getNome())) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (isBlank(request.getEmail()) || !validarEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email inválido");
        }

        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }

        if (isBlank(request.getUsername())) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
    }

    private boolean validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.isBlank();
    }
}