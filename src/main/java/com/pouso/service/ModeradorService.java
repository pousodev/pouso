package com.pouso.service;

import com.pouso.model.Animal;
import com.pouso.repository.ModeradorRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ModeradorService {

    private final ModeradorRepository moderadorRepository;

    public ModeradorService(ModeradorRepository moderadorRepository) {
        this.moderadorRepository = moderadorRepository;
    }

    public List<Animal> getModerationHistory() {
        return moderadorRepository.getModerationHistory();
    }

    public List<Animal> getPendingModerations() {
        return moderadorRepository.findPendingModerations();
    }

    public void approvePet(String petNome, String petDono, String adminCpf) {
        moderadorRepository.updateModerationStatus(petNome, petDono, adminCpf);
    }

    public void rejectPet(String petNome, String petDono, String adminCpf) {
        moderadorRepository.updateModerationStatus(petNome, petDono, adminCpf);
    }
}