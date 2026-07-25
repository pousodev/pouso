package com.pouso.controller;

import com.pouso.repository.AdoptionRepository;
import com.pouso.repository.PetRepository;
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

    public AdoptionController(AdoptionRepository adoptionRepository, PetRepository petRepository) {
        this.adoptionRepository = adoptionRepository;
        this.petRepository = petRepository;
    }

    @GetMapping("/adocoes")
    public String myAdoptions(HttpSession session, Model model) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) return "redirect:/login";

        model.addAttribute("asDonor", adoptionRepository.listActiveAsDonor(cpf));
        model.addAttribute("requests", adoptionRepository.listRequestsForOwner(cpf));
        model.addAttribute("history", adoptionRepository.listHistory(cpf));
        model.addAttribute("rejected", petRepository.listRejectedByOwner(cpf));
        return "pet/my-adoptions";
    }

    @GetMapping("/adocoes/status")
    public String adoptionStatus(@RequestParam LocalDate startDate,
                                 @RequestParam String adopterCpf,
                                 @RequestParam String petName,
                                 @RequestParam String petOwner,
                                 HttpSession session,
                                 Model model) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) return "redirect:/login";

        return adoptionRepository.findStatusForParticipant(startDate, adopterCpf, petName, petOwner, cpf)
            .map(adoption -> {
                model.addAttribute("adoption", adoption);
                return "pet/adoption-status";
            })
            .orElse("redirect:/adocoes");
    }

    @PostMapping("/adocoes/solicitacoes/aceitar")
    public String acceptRequest(
        @RequestParam LocalDate startDate,
        @RequestParam String adopterCpf,
        @RequestParam String petName,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) return "redirect:/login";

        String phone = adoptionRepository.acceptRequest(startDate, adopterCpf, petName, cpf);
        if (phone == null) {
            redirectAttributes.addFlashAttribute("error", "Solicitacao nao encontrada.");
        } else {
            redirectAttributes.addFlashAttribute("success", "Seu pet foi adotado :) Telefone do adotante: " + phone);
        }
        return "redirect:/adocoes";
    }

    @PostMapping("/adocoes/solicitacoes/recusar")
    public String rejectRequest(
        @RequestParam LocalDate startDate,
        @RequestParam String adopterCpf,
        @RequestParam String petName,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) return "redirect:/login";

        if (adoptionRepository.rejectRequest(startDate, adopterCpf, petName, cpf)) {
            redirectAttributes.addFlashAttribute("success", "Solicitacao recusada.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Solicitacao nao encontrada.");
        }
        return "redirect:/adocoes";
    }
}
