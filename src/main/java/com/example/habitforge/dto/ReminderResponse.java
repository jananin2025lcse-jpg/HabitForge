package com.example.habitforge.dto;

import java.time.LocalDate;

public record ReminderResponse(Long habitId, String habit, LocalDate date, String message) {
}