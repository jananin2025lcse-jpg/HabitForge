package com.example.habitforge.service;

import com.example.habitforge.dto.ReminderResponse;
import com.example.habitforge.model.CompletionLog;
import com.example.habitforge.model.CompletionStatus;
import com.example.habitforge.model.Habit;
import com.example.habitforge.repository.CompletionLogRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReminderService {

    private final HabitService habitService;
    private final CompletionLogRepository completionLogRepository;

    public ReminderService(HabitService habitService,
                           CompletionLogRepository completionLogRepository) {
        this.habitService = habitService;
        this.completionLogRepository = completionLogRepository;
    }

    public ReminderResponse getReminder(Long habitId) {

        Habit habit = habitService.findById(habitId);

        LocalDate today = LocalDate.now();

        List<CompletionLog> logs =
                completionLogRepository.findByHabitHabitIdOrderByCompletionDateAsc(habitId);

        boolean completedToday = logs.stream()
                .anyMatch(log ->
                        log.getCompletionDate().equals(today)
                        && log.getStatus() == CompletionStatus.COMPLETED
                );

        String message;

        if (completedToday) {
            message = "You have already completed your habit today.";
        } else {
            message = "Remember to complete your habit today.";
        }

        return new ReminderResponse(
                habit.getHabitId(),
                habit.getName(),
                today,
                message
        );
    }
}