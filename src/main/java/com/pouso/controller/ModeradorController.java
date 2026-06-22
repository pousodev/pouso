package com.pouso.controller;

import com.pouso.model.Animal;
import com.pouso.service.ModeradorService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/moderador")
public class ModeradorController {
    private final ModeradorService moderadorService;

    public ModeradorController(ModeradorService moderadorService) {
        this.moderadorService = moderadorService;
    }

    @GetMapping("/historico")
    public String historico(Model model) {
        List<Animal> animais = moderadorService.getModerationHistory();
        model.addAttribute("animais", animais);

        return "modHistorico";
    }

    @GetMapping("/solicitacoes")
    public String solicitacoes(Model model) {
        List<Animal> animais = moderadorService.getPendingModerations();
        model.addAttribute("animais", animais);
        return "modSolicitacoes";
    }

    @PostMapping("/solicitacoes/aprovar")
    public String aprovar(
        @RequestParam String petNome,
        @RequestParam String petDono,
        HttpSession session
    ) {
        String adminCpf = (String) session.getAttribute("cpf");
        moderadorService.approvePet(petNome, petDono, adminCpf);
        return "redirect:/moderador/solicitacoes";
    }

    @PostMapping("/solicitacoes/recusar")
    public String recusar(
        @RequestParam String petNome,
        @RequestParam String petDono
    ) {
        // Para recusar, marcamos com um valor simbólico, como 'recusado'
        moderadorService.rejectPet(petNome, petDono, "0000000000"); // CPF simbólico para recusa
        return "redirect:/moderador/solicitacoes";
    }
}
