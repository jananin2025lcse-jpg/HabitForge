package com.example.habitforge.service;

import com.example.habitforge.dto.CalendarEntryResponse;
import com.example.habitforge.model.CompletionLog;
import com.example.habitforge.model.CompletionStatus;
import com.example.habitforge.model.Habit;
import com.example.habitforge.repository.CompletionLogRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {
    private final HabitService habitService;
    private final CompletionLogRepository completionLogRepository;

    public CalendarService(HabitService habitService, CompletionLogRepository completionLogRepository) {
        this.habitService = habitService;
        this.completionLogRepository = completionLogRepository;
    }

    public List<CalendarEntryResponse> getCalendar(Long habitId, YearMonth month) {
        Habit habit = habitService.findById(habitId);
        Set<LocalDate> completedDates = new HashSet<>();
        for (CompletionLog log : completionLogRepository.findByHabitHabitIdOrderByCompletionDateAsc(habitId)) {
            if (log.getStatus() == CompletionStatus.COMPLETED) completedDates.add(log.getCompletionDate());
        }
        LocalDate today = LocalDate.now();
        return month.atDay(1).datesUntil(month.plusMonths(1).atDay(1))
                .map(date -> new CalendarEntryResponse(date, status(habit, date, today, completedDates)))
                .toList();
    }

    private String status(Habit habit, LocalDate date, LocalDate today, Set<LocalDate> completedDates) {
        if (date.isBefore(habit.getStartDate())) return "NOT_STARTED";
        if (!habitService.isRequiredDay(habit, date)) return "NOT_REQUIRED";
        if (completedDates.contains(date)) return "COMPLETED";
        if (!date.isBefore(today)) return "UPCOMING";
        return "MISSED";
    }
}