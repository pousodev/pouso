package com.pouso.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pouso.model.Pet;
import com.pouso.model.PetCadastroForm;
import com.pouso.model.SaudePet;
import com.pouso.repository.PetRepository;
import com.pouso.repository.SaudePetRepository;
import com.pouso.repository.TipoPetRepository;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final SaudePetRepository saudePetRepository;
    private final TipoPetRepository tipoPetRepository;

    public PetService(
        PetRepository petRepository,
        SaudePetRepository saudePetRepository,
        TipoPetRepository tipoPetRepository
    ) {
        this.petRepository = petRepository;
        this.saudePetRepository = saudePetRepository;
        this.tipoPetRepository = tipoPetRepository;
    }

    @Transactional
    public void cadastrarPet(PetCadastroForm form, String cpfDono) {
        validar(form);

        int tipoPetId = tipoPetRepository.buscarOuCriarPorNome(
            form.getEspecie().trim()
        );

        Pet pet = new Pet(
            form.getNome().trim(),
            cpfDono,
            form.getBio(),
            form.getSexo(),
            tipoPetId,
            form.getDataNascimento(),
            LocalDate.now(),
            form.getPorte(),
            form.isCastrado()
        );
        petRepository.salvar(pet);

        boolean condicaoEspecial =
            form.getCondicoesEspeciais() != null &&
            !form.getCondicoesEspeciais().isBlank();

        SaudePet saudePet = new SaudePet(
            pet.getNome(),
            cpfDono,
            form.isUsaMedicamento(),
            form.getMedicamentos(),
            condicaoEspecial,
            form.getCondicoesEspeciais()
        );
        saudePetRepository.salvar(saudePet);
    }

    private void validar(PetCadastroForm form) {
        if (form.getNome() == null || form.getNome().isBlank()) {
            throw new PetValidationException("Nome do pet é obrigatório");
        }
        if (form.getEspecie() == null || form.getEspecie().isBlank()) {
            throw new PetValidationException("Espécie é obrigatória");
        }
        if (form.getSexo() == null || !form.getSexo().matches("[MF]")) {
            throw new PetValidationException("Sexo é obrigatório");
        }
        if (form.getPorte() == null || !form.getPorte().matches("[PMG]")) {
            throw new PetValidationException("Porte é obrigatório");
        }
        if (form.getDataNascimento() == null) {
            throw new PetValidationException(
                "Data de nascimento é obrigatória"
            );
        }
        if (form.getDataNascimento().isAfter(LocalDate.now())) {
            throw new PetValidationException("Data de nascimento inválida");
        }
    }
}
