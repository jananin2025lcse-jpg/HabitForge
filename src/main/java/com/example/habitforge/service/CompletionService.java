package com.example.habitforge.service;

import com.example.habitforge.dto.CompletionRequest;
import com.example.habitforge.exception.BadRequestException;
import com.example.habitforge.exception.DuplicateCompletionException;
import com.example.habitforge.model.CompletionLog;
import com.example.habitforge.model.CompletionStatus;
import com.example.habitforge.model.Habit;
import com.example.habitforge.repository.CompletionLogRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompletionService {
    private final CompletionLogRepository completionLogRepository;
    private final HabitService habitService;
    private final StreakService streakService;

    public CompletionService(CompletionLogRepository completionLogRepository,
                             HabitService habitService, StreakService streakService) {
        this.completionLogRepository = completionLogRepository;
        this.habitService = habitService;
        this.streakService = streakService;
    }

    @Transactional
    public CompletionLog complete(Long habitId, CompletionRequest request) {
        Habit habit = habitService.findById(habitId);
        LocalDate date = request.getCompletionDate();
        LocalDate today = LocalDate.now();
        if (!habit.isActive()) throw new BadRequestException("Inactive habits cannot be completed");
        if (date.isBefore(habit.getStartDate())) {
            throw new BadRequestException("Completion date cannot be before the habit start date");
        }
        if (date.isAfter(today)) throw new BadRequestException("Completion date cannot be in the future");
        if (!habitService.isRequiredDay(habit, date)) {
            throw new BadRequestException("The completion date is not a required habit day");
        }
        if (completionLogRepository.existsByHabitHabitIdAndCompletionDate(habitId, date)) {
            throw new DuplicateCompletionException("Habit is already completed for " + date);
        }
        CompletionLog log = new CompletionLog();
        log.setHabit(habit);
        log.setCompletionDate(date);
        log.setStatus(CompletionStatus.COMPLETED);
        CompletionLog saved = completionLogRepository.save(log);
        streakService.recalculate(habit);
        return saved;
    }
}