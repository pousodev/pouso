package com.pouso.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class AdoptionSummaryTests {

    @Test
    void formatsDisplayFields() {
        AdoptionSummary adoption = new AdoptionSummary(
            LocalDate.of(2026, 7, 1), "22222222222", "Carlos", "Bob", "33333333642", "Victor",
            LocalDate.of(2026, 8, 1), LocalDate.of(2026, 6, 29), "EM_ANDAMENTO", false,
            "Cachorro", "Vira-lata", "F", "P", LocalDate.now().minusYears(2), null, true
        );

        assertThat(adoption.getStatusDescription()).isEqualTo("Em andamento");
        assertThat(adoption.getSizeDescription()).isEqualTo("Pequeno");
        assertThat(adoption.getDeadlineDescription()).isEqualTo("01/08/2026");
        assertThat(adoption.getTypeDescription()).isEqualTo("Temporaria");
        assertThat(adoption.getIdHtml()).doesNotContain("-");
    }

    @Test
    void describesRequestedStatus() {
        AdoptionSummary adoption = new AdoptionSummary(
            LocalDate.of(2026, 7, 1), "22222222222", "Carlos", "Bob", "33333333642", "Victor",
            null, null, "SOLICITADA", true, "Cachorro", "Vira-lata", "F", "P", null, null, false
        );

        assertThat(adoption.getStatusDescription()).isEqualTo("Solicitada");
    }

    @Test
    void describesPermanentInProgressAsAdopted() {
        AdoptionSummary adoption = new AdoptionSummary(
            LocalDate.of(2026, 7, 1), "22222222222", "Carlos", "Bob", "33333333642", "Victor",
            null, null, "EM_ANDAMENTO", true, "Cachorro", "Vira-lata", "F", "P", null, null, false
        );

        assertThat(adoption.getStatusDescription()).isEqualTo("Adotado");
    }

    @Test
    void calculatesTemporaryProgressFromRemainingTime() {
        AdoptionSummary adoption = new AdoptionSummary(
            LocalDate.of(2026, 7, 1), "22222222222", "Carlos", "Bob", "33333333642", "Victor",
            LocalDate.of(2026, 7, 31), null, "EM_ANDAMENTO", false,
            "Cachorro", "Vira-lata", "F", "P", null, null, false
        );

        assertThat(adoption.getStatusProgress(LocalDate.of(2026, 7, 1))).isZero();
        assertThat(adoption.getStatusProgress(LocalDate.of(2026, 7, 16))).isEqualTo(50);
        assertThat(adoption.getStatusProgress(LocalDate.of(2026, 7, 31))).isEqualTo(100);
    }
}
