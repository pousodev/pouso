package com.pouso.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class AdoptionSummary {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private LocalDate startDate;
    private String adopterCpf;
    private String adopterName;
    private String petName;
    private String petOwner;
    private String ownerName;
    private String ownerUsername;
    private String adopterUsername;
    private String adopterPhone;
    private String adopterPhoto;
    private LocalDate endDate;
    private LocalDate requestDate;
    private String status;
    private Boolean permanent;
    private String speciesName;
    private String breedName;
    private String sex;
    private String size;
    private LocalDate birthDate;
    private String petPhoto;
    private boolean hasReturnRequest;

    public AdoptionSummary(LocalDate startDate, String adopterCpf, String adopterName, String petName,
                          String petOwner, String ownerName, LocalDate endDate, LocalDate requestDate,
                          String status, Boolean permanent, String speciesName, String breedName,
                          String sex, String size, LocalDate birthDate, String petPhoto, boolean hasReturnRequest) {
        this(startDate, adopterCpf, adopterName, petName, petOwner, ownerName, null, null, null, null,
            endDate, requestDate, status, permanent, speciesName, breedName, sex, size, birthDate,
            petPhoto, hasReturnRequest);
    }

    public AdoptionSummary(LocalDate startDate, String adopterCpf, String adopterName, String petName,
                           String petOwner, String ownerName, String adopterUsername, String adopterPhone,
                           String adopterPhoto, String ownerUsername, LocalDate endDate, LocalDate requestDate, String status,
                           Boolean permanent, String speciesName, String breedName, String sex, String size,
                           LocalDate birthDate, String petPhoto, boolean hasReturnRequest) {
        this.startDate = startDate;
        this.adopterCpf = adopterCpf;
        this.adopterName = adopterName;
        this.petName = petName;
        this.petOwner = petOwner;
        this.ownerName = ownerName;
        this.ownerUsername = ownerUsername;
        this.adopterUsername = adopterUsername;
        this.adopterPhone = adopterPhone;
        this.adopterPhoto = adopterPhoto;
        this.endDate = endDate;
        this.requestDate = requestDate;
        this.status = status;
        this.permanent = permanent;
        this.speciesName = speciesName;
        this.breedName = breedName;
        this.sex = sex;
        this.size = size;
        this.birthDate = birthDate;
        this.petPhoto = petPhoto;
        this.hasReturnRequest = hasReturnRequest;
    }

    public LocalDate getStartDate() { return startDate; }
    public String getAdopterCpf() { return adopterCpf; }
    public String getAdopterName() { return adopterName; }
    public String getPetName() { return petName; }
    public String getPetOwner() { return petOwner; }
    public String getOwnerName() { return ownerName; }
    public String getOwnerUsername() { return ownerUsername; }
    public String getAdopterUsername() { return adopterUsername; }
    public String getAdopterPhone() { return adopterPhone; }
    public String getAdopterPhoto() { return adopterPhoto; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDate getRequestDate() { return requestDate; }
    public String getStatus() { return status; }
    public Boolean getPermanent() { return permanent; }
    public String getSpeciesName() { return speciesName; }
    public String getBreedName() { return breedName; }
    public String getSex() { return sex; }
    public String getSize() { return size; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getPetPhoto() { return petPhoto; }
    public boolean isHasReturnRequest() { return hasReturnRequest; }

    public String getAgeDescription() {
        if (birthDate == null) return "Idade desconhecida";
        Period age = Period.between(birthDate, LocalDate.now());
        if (age.getYears() > 0) return age.getYears() + (age.getYears() == 1 ? " ano" : " anos");
        return age.getMonths() + (age.getMonths() == 1 ? " mes" : " meses");
    }

    public String getSizeDescription() {
        if (size == null) return "-";
        return switch (size) {
            case "P" -> "Pequeno";
            case "M" -> "Medio";
            case "G" -> "Grande";
            default -> size;
        };
    }

    public String getStatusDescription() {
        if (status == null) return "-";
        if ("EM_ANDAMENTO".equals(status) && Boolean.TRUE.equals(permanent)) return "Adotado";
        return switch (status) {
            case "PENDENTE" -> "Aguardando decisao";
            case "EM_ANDAMENTO" -> "Em andamento";
            case "SOLICITADA" -> "Solicitada";
            case "CONCLUIDA" -> "Concluida";
            case "CANCELADA" -> "Cancelada";
            case "RECUSADA" -> "Recusada";
            default -> status;
        };
    }

    public String getDeadlineDescription() {
        return endDate == null ? "Sem prazo" : endDate.format(DATE);
    }

    public String getFormattedEndDate() {
        return endDate == null ? "-" : endDate.format(DATE);
    }

    public String getDaysUntilEndDescription() {
        if (endDate == null) return "Sem prazo de devolucao";
        long days = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
        if (days < 0) return "Prazo de devolucao encerrado";
        if (days == 0) return "Devolucao hoje";
        return "Faltam " + days + (days == 1 ? " dia" : " dias") + " para devolucao";
    }

    public int getStatusProgress() {
        return getStatusProgress(LocalDate.now());
    }

    int getStatusProgress(LocalDate today) {
        if (Boolean.TRUE.equals(permanent)) return 100;
        if (startDate == null || endDate == null) return 0;

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0) return 100;

        long elapsedDays = ChronoUnit.DAYS.between(startDate, today);
        long clampedElapsedDays = Math.max(0, Math.min(elapsedDays, totalDays));
        return (int) ((clampedElapsedDays * 100) / totalDays);
    }

    public String getTypeDescription() {
        return Boolean.TRUE.equals(permanent) ? "Permanente" : "Temporaria";
    }

    public String getFormattedStartDate() {
        return startDate == null ? "-" : startDate.format(DATE);
    }

    public String getFormattedRequestDate() {
        return requestDate == null ? "-" : requestDate.format(DATE);
    }

    public String getIdHtml() {
        return (startDate + "-" + adopterCpf + "-" + petName + "-" + petOwner).replaceAll("[^a-zA-Z0-9]", "");
    }
}
