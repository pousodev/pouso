package com.pouso.controller;

import com.pouso.dto.CadastroRequest;
import com.pouso.model.Person;
import com.pouso.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cadastro")
public class CadastroController {

    private final AuthService authService;

    public CadastroController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody CadastroRequest request) {
        try {
            Person person = authService.cadastrar(request);

            return ResponseEntity.ok().body(
                "Usuário cadastrado com sucesso. CPF: " + person.getCPF()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}