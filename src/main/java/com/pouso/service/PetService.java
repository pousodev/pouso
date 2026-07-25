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
import com.pouso.repository.UserRepository;
import com.pouso.dto.PetDetalheDTO;
import com.pouso.dto.PetOwnerListDTO;
import com.pouso.dto.PetOwnerListDTO.OwnerItem;
import com.pouso.dto.PetOwnerListDTO.PetItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final SaudePetRepository saudePetRepository;
    private final TipoPetRepository tipoPetRepository;
    private final UserRepository userRepository;

    public PetService(
        PetRepository petRepository,
        SaudePetRepository saudePetRepository,
        TipoPetRepository tipoPetRepository,
        UserRepository userRepository
    ) {
        this.petRepository = petRepository;
        this.saudePetRepository = saudePetRepository;
        this.tipoPetRepository = tipoPetRepository;
        this.userRepository = userRepository;
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

    public PetOwnerListDTO listPaged(int page, int size, String sortBy, String sortDir, String q) {
        int offset = page * size;
        List<OwnerItem> owners = petRepository.listarProprietariosPaginado(offset, size, sortBy, sortDir, q);
        long total = petRepository.contarProprietarios(q);

        if (!owners.isEmpty()) {
            List<String> cpfs = owners.stream().map(OwnerItem::getCpf).collect(Collectors.toList());
            List<PetItem> allPets = petRepository.buscarPetsPorCpfs(cpfs);

            Map<String, List<PetItem>> petsByCpf = allPets.stream()
                    .collect(Collectors.groupingBy(PetItem::getCpfDono));

            for (OwnerItem owner : owners) {
                owner.setPets(petsByCpf.getOrDefault(owner.getCpf(), List.of()));
            }
        }

        return new PetOwnerListDTO(owners, page, size, total);
    }



    private static final int TAMANHO_PAGINA = 12;
    public List<Pet> buscarPets( Boolean isPermanente,String cidade,String bairro,int pagina) {
        return petRepository.buscarPets(isPermanente,cidade,bairro,pagina,TAMANHO_PAGINA);
    }


    public int calcularTotalPaginas(Boolean isPermanente,String cidade,String bairro) {
        int totalPets = petRepository.contarPets(isPermanente,cidade,bairro );
        return (int) Math.ceil(
                (double) totalPets / TAMANHO_PAGINA
        );
    }


    public List<String> listarCidades() {
        return petRepository.listarCidades();
    }


    public List<String> listarBairros() {
        return petRepository.listarBairros();
    }

    public PetDetalheDTO buscarDetalhe(String cpfDono, String nome, String sessionCpf) {
        PetDetalheDTO pet = petRepository.buscarDetalhe(cpfDono, nome)
            .orElseThrow(() -> new PetValidationException("Pet não encontrado"));

        boolean isOwner = sessionCpf != null && sessionCpf.equals(cpfDono);
        if (!isOwner && !pet.isAprovado()) {
            throw new PetValidationException("Pet não encontrado");
        }

        return pet;
    }

    public PetDetalheDTO buscarDetalhePorUsername(String usernameDono, String nome, String sessionCpf) {
        var dono = userRepository.buscarPorUsername(usernameDono);
        if (dono == null) {
            throw new PetValidationException("Pet não encontrado");
        }
        return buscarDetalhe(dono.getCpf(), nome, sessionCpf);
    }
}
