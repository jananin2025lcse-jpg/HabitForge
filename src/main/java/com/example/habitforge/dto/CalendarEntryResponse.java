package com.example.habitforge.dto;

import java.time.LocalDate;

public record CalendarEntryResponse(LocalDate date, String status) {
}