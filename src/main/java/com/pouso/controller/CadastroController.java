package com.pouso.controller;

import com.pouso.dto.CadastroRequest;
import com.pouso.model.Person;
import com.pouso.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    private final AuthService authService;

    public CadastroController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public String cadastroScreen() {
        return "cadastro";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String cadastrarFormulario(CadastroRequest request, Model model) {
        try {
            authService.cadastrar(request);
            model.addAttribute("success", "Usuário cadastrado com sucesso!");
            return "cadastro";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "cadastro";
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> cadastrarJson(@RequestBody CadastroRequest request) {
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