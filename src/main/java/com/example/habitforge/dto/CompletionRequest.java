package com.example.habitforge.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CompletionRequest {
    @NotNull(message = "Completion date is required")
    private LocalDate completionDate;

    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }
}