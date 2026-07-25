package com.pouso.controller;

import com.pouso.repository.AdoptionRepository;
import com.pouso.repository.PetRepository;
import com.pouso.service.AdoptionService;
import com.pouso.service.PetValidationException;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdoptionController {

    private final AdoptionRepository adoptionRepository;
    private final PetRepository petRepository;
    private final AdoptionService adoptionService;

    public AdoptionController(
        AdoptionRepository adoptionRepository,
        PetRepository petRepository,
        AdoptionService adoptionService
    ) {
        this.adoptionRepository = adoptionRepository;
        this.petRepository = petRepository;
        this.adoptionService = adoptionService;
    }

    @GetMapping("/adocoes")
    public String myAdoptions(HttpSession session, Model model) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) return "redirect:/login";

        model.addAttribute("solicitacoes", adoptionService.listarSolicitacoesPendentes(cpf));
        model.addAttribute("asDonor", adoptionRepository.listActiveAsDonor(cpf));
        model.addAttribute("requests", adoptionRepository.listRequestsForOwner(cpf));
        model.addAttribute("history", adoptionRepository.listHistory(cpf));
        model.addAttribute("rejected", petRepository.listRejectedByOwner(cpf));
        return "pet/my-adoptions";
    }

    @GetMapping("/adocoes/status")
    public String adoptionStatus(@RequestParam LocalDate startDate,
                                  @RequestParam String adopterUsername,
                                  @RequestParam String petName,
                                  @RequestParam String ownerUsername,
                                  HttpSession session,
                                  Model model) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) return "redirect:/login";

        return adoptionRepository.findStatusForParticipantByUsernames(startDate, adopterUsername, petName, ownerUsername, cpf)
            .map(adoption -> {
                model.addAttribute("adoption", adoption);
                return "pet/adoption-status";
            })
            .orElse("redirect:/adocoes");
    }

    @PostMapping("/adocoes/solicitacoes/aceitar")
    public String acceptRequest(
        @RequestParam LocalDate startDate,
        @RequestParam String adopterUsername,
        @RequestParam String petName,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        return aceitar(startDate, adopterUsername, petName, session, redirectAttributes);
    }

    @PostMapping("/adocoes/solicitacoes/recusar")
    public String rejectRequest(
        @RequestParam LocalDate startDate,
        @RequestParam String adopterUsername,
        @RequestParam String petName,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        return recusar(startDate, adopterUsername, petName, session, redirectAttributes);
    }

    @PostMapping("/adocoes/aceitar")
    public String aceitar(
        @RequestParam LocalDate dataInicio,
        @RequestParam String adopterUsername,
        @RequestParam String petNome,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) return "redirect:/login";

        try {
            String phone = adoptionService.aceitarSolicitacaoPorUsername(dataInicio, adopterUsername, petNome, cpf);
            redirectAttributes.addFlashAttribute("success", "Seu pet foi adotado :) Telefone do adotante: " + phone);
        } catch (PetValidationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/adocoes";
    }

    @PostMapping("/adocoes/recusar")
    public String recusar(
        @RequestParam LocalDate dataInicio,
        @RequestParam String adopterUsername,
        @RequestParam String petNome,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) return "redirect:/login";

        try {
            adoptionService.recusarSolicitacaoPorUsername(dataInicio, adopterUsername, petNome, cpf);
            redirectAttributes.addFlashAttribute("success", "Solicitação recusada.");
        } catch (PetValidationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/adocoes";
    }
}
