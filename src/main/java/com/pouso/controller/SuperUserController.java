package com.pouso.controller;

import com.pouso.dto.PetOwnerListDTO;
import com.pouso.dto.UserListDTO;
import com.pouso.repository.UsuarioRepository;
import com.pouso.service.PetService;
import com.pouso.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SuperUserController {

    private final UsuarioRepository usuarioRepository;
    private final UserService userService;
    private final PetService petService;

    public SuperUserController(UsuarioRepository usuarioRepository, UserService userService, PetService petService) {
        this.usuarioRepository = usuarioRepository;
        this.userService = userService;
        this.petService = petService;
    }

    @GetMapping("/sudo/users")
    public String sudo(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) {
            return "redirect:/login";
        }

        String nivel = usuarioRepository.buscarNivelAdmin(cpf);
        if (!"S".equals(nivel)) {
            return "redirect:/home";
        }

        if (size != 10 && size != 20 && size != 50) {
            size = 10;
        }
        if (!sortBy.equals("nome") && !sortBy.equals("data_registro")) {
            sortBy = "nome";
        }
        if (!sortDir.equals("asc") && !sortDir.equals("desc")) {
            sortDir = "asc";
        }

        UserListDTO result = userService.listPaged(page, size, sortBy, sortDir, q);

        model.addAttribute("usuarios", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        model.addAttribute("currentSize", size);

        return "superUser";
    }

    @GetMapping("/sudo/pets")
    public String pets(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) {
        String cpf = (String) session.getAttribute("cpf");
        if (cpf == null) {
            return "redirect:/login";
        }

        String nivel = usuarioRepository.buscarNivelAdmin(cpf);
        if (!"S".equals(nivel)) {
            return "redirect:/home";
        }

        if (size != 10 && size != 20 && size != 50) {
            size = 10;
        }
        if (!sortBy.equals("nome") && !sortBy.equals("data_registro") && !sortBy.equals("pet_count")) {
            sortBy = "nome";
        }
        if (!sortDir.equals("asc") && !sortDir.equals("desc")) {
            sortDir = "asc";
        }

        PetOwnerListDTO result = petService.listPaged(page, size, sortBy, sortDir, q);

        model.addAttribute("proprietarios", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        model.addAttribute("currentSize", size);

        return "superUserPet";
    }
}
