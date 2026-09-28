package com.example.habitforge.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StreakResponse(Long habitId, int currentStreak, int bestStreak,
                             LocalDate lastCompletedDate, LocalDateTime updatedAt) {
}