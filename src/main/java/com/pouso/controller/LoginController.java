package com.pouso.controller;

import com.pouso.model.Person;
import com.pouso.repository.UsuarioRepository;
import com.pouso.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    public LoginController(AuthService authService, UsuarioRepository usuarioRepository) {
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/login")
    public String loginScreen() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
        @RequestParam String email,
        @RequestParam String password,
        HttpSession session,
        Model model
    ) {
        Person person = authService.login(email, password);

        if (person != null) {
            String cpf = person.getCPF();
            session.setAttribute("cpf", cpf);

            String nivel = usuarioRepository.buscarNivelAdmin(cpf);
            if ("S".equals(nivel)) {
                return "redirect:/sudo/users";
            }

            return "redirect:/home";
        }

        model.addAttribute("error", "Email ou senha inválidos");
        return "login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
}
