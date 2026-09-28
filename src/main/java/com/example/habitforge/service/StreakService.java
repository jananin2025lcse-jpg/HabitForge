package com.example.habitforge.service;

import com.example.habitforge.dto.StreakResponse;
import com.example.habitforge.model.CompletionLog;
import com.example.habitforge.model.CompletionStatus;
import com.example.habitforge.model.Habit;
import com.example.habitforge.model.Streak;
import com.example.habitforge.repository.CompletionLogRepository;
import com.example.habitforge.repository.StreakRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StreakService {
    private final CompletionLogRepository completionLogRepository;
    private final StreakRepository streakRepository;
    private final HabitService habitService;

    public StreakService(CompletionLogRepository completionLogRepository,
                         StreakRepository streakRepository, HabitService habitService) {
        this.completionLogRepository = completionLogRepository;
        this.streakRepository = streakRepository;
        this.habitService = habitService;
    }

    @Transactional
    public StreakResponse getStreak(Long habitId) {
        Habit habit = habitService.findById(habitId);
        return toResponse(recalculate(habit));
    }

    @Transactional
    public Streak recalculate(Habit habit) {
        List<CompletionLog> logs = completionLogRepository.findByHabitHabitIdOrderByCompletionDateAsc(habit.getHabitId());
        Set<LocalDate> completedDates = new HashSet<>();
        LocalDate lastCompleted = null;
        for (CompletionLog log : logs) {
            if (log.getStatus() == CompletionStatus.COMPLETED
                    && !log.getCompletionDate().isAfter(LocalDate.now())) {
                completedDates.add(log.getCompletionDate());
                if (lastCompleted == null || log.getCompletionDate().isAfter(lastCompleted)) {
                    lastCompleted = log.getCompletionDate();
                }
            }
        }
        LocalDate today = LocalDate.now();
        int current = 0;
        LocalDate cursor = today;
        while (!cursor.isBefore(habit.getStartDate())) {
            if (habitService.isRequiredDay(habit, cursor)) {
                if (cursor.equals(today) && !completedDates.contains(cursor)) {
                    cursor = cursor.minusDays(1);
                    continue;
                }
                if (!completedDates.contains(cursor)) break;
                current++;
            }
            cursor = cursor.minusDays(1);
        }

        int best = 0;
        int run = 0;
        for (LocalDate date = habit.getStartDate(); !date.isAfter(today); date = date.plusDays(1)) {
            if (!habitService.isRequiredDay(habit, date)) continue;
            if (completedDates.contains(date)) {
                run++;
                best = Math.max(best, run);
            } else {
                run = 0;
            }
        }

        Streak streak = streakRepository.findByHabitHabitId(habit.getHabitId()).orElseGet(() -> {
            Streak created = new Streak();
            created.setHabit(habit);
            return created;
        });
        streak.setCurrentStreak(current);
        streak.setBestStreak(Math.max(streak.getBestStreak(), best));
        streak.setLastCompletedDate(lastCompleted);
        streak.setUpdatedAt(LocalDateTime.now());
        return streakRepository.save(streak);
    }

    private StreakResponse toResponse(Streak streak) {
        return new StreakResponse(streak.getHabit().getHabitId(), streak.getCurrentStreak(),
                streak.getBestStreak(), streak.getLastCompletedDate(), streak.getUpdatedAt());
    }
}