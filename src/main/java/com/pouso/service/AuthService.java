package com.pouso.service;

import com.pouso.dto.CadastroRequest;
import com.pouso.model.Person;
import com.pouso.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final PersonRepository personRepository;

    public AuthService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public Person cadastrar(CadastroRequest request) {
        validarCadastro(request);

        if (personRepository.cpfExiste(request.getCpf())) {
            throw new IllegalArgumentException("CPF ja cadastrado");
        }

        if (personRepository.emailExiste(request.getEmail())) {
            throw new IllegalArgumentException("Email ja cadastrado");
        }

        personRepository.inserirPessoa(
            request.getCpf(),
            request.getNome(),
            request.getEmail(),
            request.getPassword()
        );

        personRepository.inserirUsuario(
            request.getCpf(),
            request.getUsername(),
            request.getTelefone(),
            request.getGenero()
        );

        personRepository.inserirEndereco(
            request.getCpf(),
            request.getCep(),
            request.getRua(),
            request.getNumero(),
            request.getComplemento(),
            request.getBairro(),
            request.getCidade(),
            request.getUf()
        );

        return new Person(
            request.getCpf(),
            request.getNome(),
            request.getEmail(),
            request.getPassword()
        );
    }

    public Person login(String email, String password) {
        if (isBlank(email) || isBlank(password)) {
            return null;
        }

        if (!validarEmail(email)) {
            return null;
        }

        Person person = personRepository.buscarPorEmail(email);

        if (person == null) {
            return null;
        }

        if (personRepository.isUsuarioBanido(person.getCPF())) {
            return null;
        }

        if (!person.getPassword().equals(password)) {
            return null;
        }

        return person;
    }

    private void validarCadastro(CadastroRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do cadastro sao obrigatorios");
        }

        if (isBlank(request.getCpf()) || request.getCpf().length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 digitos");
        }

        if (isBlank(request.getNome())) {
            throw new IllegalArgumentException("Nome e obrigatorio");
        }

        if (isBlank(request.getEmail()) || !validarEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email invalido");
        }

        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException("Senha e obrigatoria");
        }

        if (isBlank(request.getUsername())) {
            throw new IllegalArgumentException("Username e obrigatorio");
        }

        if (isBlank(request.getTelefone()) || request.getTelefone().length() != 11) {
            throw new IllegalArgumentException("Telefone deve ter 11 digitos");
        }

        if (isBlank(request.getGenero()) ||
            (!request.getGenero().equals("M") &&
            !request.getGenero().equals("F") &&
            !request.getGenero().equals("O"))) {
            throw new IllegalArgumentException("Genero invalido");
        }

        if (isBlank(request.getCep()) || request.getCep().length() != 8) {
            throw new IllegalArgumentException("CEP deve ter 8 digitos");
        }

        if (isBlank(request.getRua())) {
            throw new IllegalArgumentException("Rua e obrigatoria");
        }

        if (isBlank(request.getNumero())) {
            throw new IllegalArgumentException("Numero e obrigatorio");
        }

        if (isBlank(request.getBairro())) {
            throw new IllegalArgumentException("Bairro e obrigatorio");
        }

        if (isBlank(request.getCidade())) {
            throw new IllegalArgumentException("Cidade e obrigatoria");
        }

        if (isBlank(request.getUf()) || request.getUf().length() != 2) {
            throw new IllegalArgumentException("Estado deve ter 2 letras");
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
