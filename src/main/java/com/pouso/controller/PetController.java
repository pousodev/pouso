package com.pouso.controller;

import com.pouso.model.PetCadastroForm;
import com.pouso.service.PetService;
import com.pouso.service.PetValidationException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/cadastrar")
    public String cadastrarForm(HttpSession session, Model model) {
        // TODO: checagem de login removida temporariamente para visualizar a
        // tela antes de existir cadastro de usuário. Reativar (descomentar)
        // quando houver login disponível para teste:
        // String cpf = (String) session.getAttribute("cpf");
        // if (cpf == null) {
        //     return "redirect:/login";
        // }

        if (!model.containsAttribute("petForm")) {
            model.addAttribute("petForm", new PetCadastroForm());
        }

        return "cadastro-pet";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(
        @ModelAttribute("petForm") PetCadastroForm form,
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) {
            return "redirect:/login";
        }

        try {
            petService.cadastrarPet(form, cpf);
        } catch (PetValidationException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("petForm", form);
            return "cadastro-pet";
        }

        redirectAttributes.addFlashAttribute(
            "success",
            "Pet enviado para validação com sucesso!"
        );
        return "redirect:/pets/cadastrar";
    }
}
