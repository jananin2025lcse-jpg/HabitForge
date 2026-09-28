package com.example.habitforge.repository;

import com.example.habitforge.model.CompletionLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletionLogRepository extends JpaRepository<CompletionLog, Long> {
    boolean existsByHabitHabitIdAndCompletionDate(Long habitId, java.time.LocalDate completionDate);
    List<CompletionLog> findByHabitHabitIdOrderByCompletionDateAsc(Long habitId);
}