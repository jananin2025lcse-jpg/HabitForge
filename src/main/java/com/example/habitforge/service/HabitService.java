package com.example.habitforge.service;

import com.example.habitforge.exception.BadRequestException;
import com.example.habitforge.exception.ResourceNotFoundException;
import com.example.habitforge.model.FrequencyType;
import com.example.habitforge.model.Habit;
import com.example.habitforge.model.Streak;
import com.example.habitforge.repository.HabitRepository;
import com.example.habitforge.repository.StreakRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HabitService {
    private final HabitRepository habitRepository;
    private final StreakRepository streakRepository;

    public HabitService(HabitRepository habitRepository, StreakRepository streakRepository) {
        this.habitRepository = habitRepository;
        this.streakRepository = streakRepository;
    }

    @Transactional
    public Habit create(Habit habit) {
        validateSchedule(habit);
        Habit saved = habitRepository.save(habit);
        Streak streak = new Streak();
        streak.setHabit(saved);
        streak.setUpdatedAt(java.time.LocalDateTime.now());
        streakRepository.save(streak);
        return saved;
    }

    public List<Habit> findAll() { return habitRepository.findAll(); }

    public Habit findById(Long id) {
        return habitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habit not found: " + id));
    }

    @Transactional
    public Habit update(Long id, Habit replacement) {
        Habit habit = findById(id);
        validateSchedule(replacement);
        habit.setName(replacement.getName());
        habit.setDescription(replacement.getDescription());
        habit.setFrequencyType(replacement.getFrequencyType());
        habit.setTargetDays(replacement.getTargetDays());
        habit.setStartDate(replacement.getStartDate());
        habit.setActive(replacement.isActive());
        return habitRepository.save(habit);
    }

    @Transactional
    public void delete(Long id) {
        Habit habit = findById(id);
        habitRepository.delete(habit);
    }

    public void validateSchedule(Habit habit) {
        if (habit.getName() == null || habit.getName().isBlank()) {
            throw new BadRequestException("Habit name is required");
        }
        if (habit.getStartDate() == null) {
            throw new BadRequestException("Start date is required");
        }
        if (habit.getFrequencyType() == null) {
            throw new BadRequestException("Frequency type is required");
        }
        Set<DayOfWeek> targetDays = habit.getTargetDays();
        if (habit.getFrequencyType() == FrequencyType.SPECIFIC_WEEKDAYS
                && (targetDays == null || targetDays.isEmpty())) {
            throw new BadRequestException("Target days are required for SPECIFIC_WEEKDAYS");
        }
        if (habit.getFrequencyType() == FrequencyType.DAILY) {
            habit.setTargetDays(null);
        } else {
            habit.setTargetDays(new HashSet<>(targetDays));
        }
    }

    public boolean isRequiredDay(Habit habit, LocalDate date) {
        if (date.isBefore(habit.getStartDate())) return false;
        return habit.getFrequencyType() == FrequencyType.DAILY
                || (habit.getTargetDays() != null && habit.getTargetDays().contains(date.getDayOfWeek()));
    }

    public Set<DayOfWeek> targetDaysOrEmpty(Habit habit) {
        return habit.getTargetDays() == null ? Collections.emptySet() : habit.getTargetDays();
    }
}