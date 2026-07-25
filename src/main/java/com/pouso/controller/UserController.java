package com.pouso.controller;

import com.pouso.model.Endereco;
import com.pouso.model.User;
import com.pouso.service.UserService;
import com.pouso.repository.PetRepository;
import com.pouso.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final UserService userService;

    public UserController(
        UserRepository userRepository,
        PetRepository petRepository,
       UserService userService
    ) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.userService = userService;
    }

    @GetMapping("/user")
    public String editUser(
        HttpSession session,
        Model model
    ) {
        String cpf = (String) session.getAttribute("cpf");

        if (cpf == null) {
            return "redirect:/login";
        }

        User usuario = userRepository.buscarPorCpf(cpf);

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("endereco", userRepository.buscarEnderecoPorCpf(cpf));

        return "user/edit";
    }

    @PostMapping("/conta/salvar")
    public String saveUser(
        @RequestParam String nome,
        @RequestParam String email,
        @RequestParam String username,
        @RequestParam(required = false) String bio,
        @RequestParam(required = false) String genero,
        @RequestParam(required = false) String telefone,
        @RequestParam String cep,
        @RequestParam String rua,
        @RequestParam String numero,
        @RequestParam(required = false) String complemento,
        @RequestParam String bairro,
        @RequestParam String cidade,
        @RequestParam String uf,
        @RequestParam(required = false) String currentPassword,
        @RequestParam(required = false) String newPassword,
        @RequestParam(required = false) String confirmPassword,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        String cpf = (String) session.getAttribute("cpf");

        if (cpf == null) {
            return "redirect:/login";
        }

        User usuario = userRepository.buscarPorCpf(cpf);

        if (usuario == null) {
            return "redirect:/";
        }

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setUsername(username);
        usuario.setBio(bio);
        usuario.setGenero(genero);
        usuario.setTelefone(telefone);

        if (senhaFoiPreenchida(currentPassword, newPassword, confirmPassword)) {
            String erroSenha = validarAlteracaoSenha(
                usuario,
                currentPassword,
                newPassword,
                confirmPassword
            );

            if (erroSenha != null) {
                redirectAttributes.addFlashAttribute("error", erroSenha);
                return "redirect:/user";
            }

            usuario.setSenha(newPassword);
        }

        Endereco endereco = new Endereco(
            cpf,
            cep,
            rua,
            numero,
            complemento,
            bairro,
            cidade,
            uf
        );

        userRepository.atualizarComEndereco(usuario, endereco);

        redirectAttributes.addFlashAttribute(
            "success",
            "Alterações salvas com sucesso."
        );

        return "redirect:/user";
    }

    @GetMapping("/perfil")
    public String profile(
        HttpSession session,
        Model model
    ) {
        String sessionCpf = (String) session.getAttribute("cpf");

        if (sessionCpf == null) {
            return "redirect:/login";
        }

        User profileUser = userRepository.buscarPorCpf(sessionCpf);

        if (profileUser == null) {
            return "redirect:/";
        }

        return renderProfile(model, profileUser, sessionCpf);
    }

    @GetMapping("/perfil/{username}")
    public String profileByUsername(
        @PathVariable String username,
        HttpSession session,
        Model model
    ) {
        String sessionCpf = (String) session.getAttribute("cpf");

        if (sessionCpf == null) {
            return "redirect:/login";
        }

        User profileUser = userRepository.buscarPorUsername(username);

        if (profileUser == null) {
            return "redirect:/";
        }

        return renderProfile(model, profileUser, sessionCpf);
    }

    private String renderProfile(
        Model model,
        User profileUser,
        String sessionCpf
    ) {
        boolean isSelf = sessionCpf.equals(profileUser.getCpf());

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("isSelf", isSelf);
        model.addAttribute("canEdit", isSelf);
        model.addAttribute("canDelete", false);
        model.addAttribute(
            "rating",
            userRepository.mediaAvaliacoesRecebidas(profileUser.getCpf())
        );
        model.addAttribute(
            "reviewCount",
            userRepository.contarAvaliacoesRecebidas(profileUser.getCpf())
        );
        model.addAttribute(
            "adoptionCount",
            userRepository.contarAdocoesDosPets(profileUser.getCpf())
        );
        model.addAttribute(
            "location",
            userRepository.buscarLocalizacao(profileUser.getCpf())
        );

        Endereco endereco = userRepository.buscarEnderecoPorCpf(profileUser.getCpf());
        model.addAttribute("endereco", endereco);
        model.addAttribute("enderecoCompleto", formatarEndereco(endereco));

        model.addAttribute(
            "pets",
            petRepository.listarAprovadosPorDono(profileUser.getCpf())
        );
        model.addAttribute(
            "reviews",
            userRepository.listarAvaliacoesRecebidas(profileUser.getCpf())
        );

        return "user/profile";
    }

    private String formatarEndereco(Endereco endereco) {
        if (endereco == null || isBlank(endereco.getRua())) {
            return "Endere\u00e7o n\u00e3o informado.";
        }

        String linha = endereco.getRua();

        if (!isBlank(endereco.getNumero())) {
            linha += ", " + endereco.getNumero();
        }

        if (!isBlank(endereco.getComplemento())) {
            linha += " - " + endereco.getComplemento();
        }

        if (!isBlank(endereco.getBairro())) {
            linha += ", " + endereco.getBairro();
        }

        if (!isBlank(endereco.getCidade())) {
            linha += ", " + endereco.getCidade();
        }

        if (!isBlank(endereco.getUf())) {
            linha += "/" + endereco.getUf();
        }

        if (!isBlank(endereco.getCep())) {
            linha += " - CEP " + endereco.getCep();
        }

        return linha;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean senhaFoiPreenchida(
        String currentPassword,
        String newPassword,
        String confirmPassword
    ) {
        return !isBlank(currentPassword) ||
            !isBlank(newPassword) ||
            !isBlank(confirmPassword);
    }

    private String validarAlteracaoSenha(
        User usuario,
        String currentPassword,
        String newPassword,
        String confirmPassword
    ) {
        if (isBlank(currentPassword)) {
            return "Informe a senha atual para alterar sua senha.";
        }

        if (!currentPassword.equals(usuario.getSenha())) {
            return "Senha atual incorreta.";
        }

        if (isBlank(newPassword)) {
            return "Informe a nova senha.";
        }

        if (isBlank(confirmPassword)) {
            return "Confirme a nova senha.";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "A nova senha e a confirma\u00e7\u00e3o n\u00e3o conferem.";
        }

        return null;
    }

    @GetMapping("/notifications")
    public String notifications(
        HttpSession session,
        Model model
    ) {
        String cpf = (String) session.getAttribute("cpf");

        if (cpf == null) {
            return "redirect:/login";
        }
    model.addAttribute(
        "notifications",
        userService.listarNotificacoes(cpf)
    );

        return "user/notifications";
    }
}
