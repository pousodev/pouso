package com.pouso.controller;

import com.pouso.model.PetCadastroForm;
import com.pouso.repository.AdoptionRepository;
import com.pouso.repository.PetRepository;
import com.pouso.service.AdoptionService;
import com.pouso.service.PetService;
import com.pouso.service.PetValidationException;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;
    private final PetRepository petRepository;
    private final AdoptionRepository adoptionRepository;
    private final AdoptionService adoptionService;

    public PetController(
        PetService petService,
        PetRepository petRepository,
        AdoptionRepository adoptionRepository,
        AdoptionService adoptionService
    ) {
        this.petService = petService;
        this.petRepository = petRepository;
        this.adoptionRepository = adoptionRepository;
        this.adoptionService = adoptionService;
    }

    @GetMapping
    public String myPets(HttpSession session, Model model) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) return "redirect:/login";

        model.addAttribute("myPets", petRepository.listByOwner(cpf));
        model.addAttribute("currentAdoptionsByPet", adoptionRepository.listCurrentForOwnerPets(cpf).stream()
                .collect(Collectors.toMap(a -> a.getPetOwner() + "|" + a.getPetName(), a -> a, (first, ignored) -> first)));
        model.addAttribute("adopting", adoptionRepository.listCurrentAndRequestedAsAdopter(cpf));
        return "pet/my-pets";
    }

    @GetMapping("/cadastrar")
    public String cadastrarForm(HttpSession session, Model model) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) {
            return "redirect:/login";
        }

        if (!model.containsAttribute("petForm")) {
            model.addAttribute("petForm", new PetCadastroForm());
        }

        return "pet/cadastro";
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
            return "pet/cadastro";
        }

        redirectAttributes.addFlashAttribute(
            "success",
            "Pet enviado para validação com sucesso!"
        );
        return "redirect:/pets/cadastrar";
    };

    @GetMapping("/search")
    public String buscarPets(

            @RequestParam(required = false) Boolean isPermanente,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String bairro,
            @RequestParam(defaultValue = "1") int pagina,

            HttpSession session,
            Model model
    ) {
        // Converte "" para null
        if (cidade != null && cidade.isBlank()) {
            cidade = null;
        }

        if (bairro != null && bairro.isBlank()) {
            bairro = null;
        }

        model.addAttribute(
                "pets",
                petService.buscarPets(
                        isPermanente,
                        cidade,
                        bairro,
                        pagina
                )
        );

        model.addAttribute(
                "cidades",
                petService.listarCidades()
        );

        model.addAttribute(
                "bairros",
                petService.listarBairros()
        );

        model.addAttribute("paginaAtual",pagina );

        model.addAttribute( "totalPaginas",
                petService.calcularTotalPaginas(
                        isPermanente,
                        cidade,
                        bairro
                )
        );

        // Mantém os filtros selecionados
        model.addAttribute("tipoSelecionado", isPermanente);
        model.addAttribute("cidadeSelecionada", cidade);
        model.addAttribute("bairroSelecionado", bairro);

        return "pet/search";
    }

    @GetMapping("/{username}/{nome}")
    public String detalhe(
        @RequestParam(required = false) String from,
        @PathVariable String username,
        @PathVariable String nome,
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        String sessionCpf = (String) session.getAttribute("cpf");

        try {
            var pet = petService.buscarDetalhePorUsername(username, nome, sessionCpf);
            model.addAttribute("pet", pet);
            model.addAttribute("isOwner", sessionCpf != null && sessionCpf.equals(pet.getCpfDono()));
            model.addAttribute("loggedIn", sessionCpf != null);
            model.addAttribute("backUrl", "adocoes".equals(from) ? "/adocoes" : "/pets/search");
            return "pet/perfil";
        } catch (PetValidationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/pets/search";
        }
    }

    @PostMapping("/{username}/{nome}/adotar")
    public String solicitarAdocao(
        @PathVariable String username,
        @PathVariable String nome,
        @RequestParam boolean permanente,
        @RequestParam(required = false) LocalDate dataFim,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        String sessionCpf = (String) session.getAttribute("cpf");
        if (sessionCpf == null) {
            return "redirect:/login";
        }

        try {
            var pet = petService.buscarDetalhePorUsername(username, nome, sessionCpf);
            adoptionService.solicitarAdocao(pet.getCpfDono(), nome, permanente, dataFim, sessionCpf);
            redirectAttributes.addFlashAttribute(
                "success",
                "Solicitação enviada! Acompanhe em Minhas adoções."
            );
        } catch (PetValidationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/pets/{username}/{nome}";
    }
}
