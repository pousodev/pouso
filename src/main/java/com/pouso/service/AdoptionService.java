package com.pouso.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pouso.dto.PetDetalheDTO;
import com.pouso.model.AdoptionSummary;
import com.pouso.repository.AdoptionRepository;

@Service
public class AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final PetService petService;

    public AdoptionService(AdoptionRepository adoptionRepository, PetService petService) {
        this.adoptionRepository = adoptionRepository;
        this.petService = petService;
    }

    public void solicitarAdocao(
        String cpfDono,
        String petNome,
        boolean permanente,
        LocalDate dataFim,
        String sessionCpf
    ) {
        if (sessionCpf.equals(cpfDono)) {
            throw new PetValidationException(
                "Você não pode adotar seu próprio pet"
            );
        }

        PetDetalheDTO pet = petService.buscarDetalhe(cpfDono, petNome, sessionCpf);
        if (!pet.isDisponivel()) {
            throw new PetValidationException(
                "Este pet não está disponível para adoção"
            );
        }

        adoptionRepository.solicitar(sessionCpf, petNome, cpfDono, permanente, dataFim);
    }

    public String aceitarSolicitacao(
        LocalDate dataInicio,
        String cpfAdotante,
        String petNome,
        String petDono,
        String sessionCpf
    ) {
        if (!sessionCpf.equals(petDono)) {
            throw new PetValidationException("Sem permissão para essa ação");
        }
        String phone = adoptionRepository.aceitar(dataInicio, cpfAdotante, petNome, petDono);
        if (phone == null) {
            throw new PetValidationException("Solicitação não encontrada.");
        }
        return phone;
    }

    public String aceitarSolicitacaoPorUsername(
        LocalDate dataInicio,
        String adopterUsername,
        String petNome,
        String sessionCpf
    ) {
        String phone = adoptionRepository.aceitarPorUsernames(dataInicio, adopterUsername, petNome, sessionCpf);
        if (phone == null) {
            throw new PetValidationException("Solicitação não encontrada.");
        }
        return phone;
    }

    public void recusarSolicitacao(
        LocalDate dataInicio,
        String cpfAdotante,
        String petNome,
        String petDono,
        String sessionCpf
    ) {
        if (!sessionCpf.equals(petDono)) {
            throw new PetValidationException("Sem permissão para essa ação");
        }
        if (!adoptionRepository.recusar(dataInicio, cpfAdotante, petNome, petDono)) {
            throw new PetValidationException("Solicitação não encontrada.");
        }
    }

    public void recusarSolicitacaoPorUsername(
        LocalDate dataInicio,
        String adopterUsername,
        String petNome,
        String sessionCpf
    ) {
        if (!adoptionRepository.recusarPorUsernames(dataInicio, adopterUsername, petNome, sessionCpf)) {
            throw new PetValidationException("Solicitação não encontrada.");
        }
    }

    public List<AdoptionSummary> listarSolicitacoesPendentes(String cpf) {
        return adoptionRepository.listPendentesAsDonor(cpf);
    }
}
