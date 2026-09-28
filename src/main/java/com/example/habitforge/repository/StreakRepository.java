package com.example.habitforge.repository;

import com.example.habitforge.model.Streak;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreakRepository extends JpaRepository<Streak, Long> {
    Optional<Streak> findByHabitHabitId(Long habitId);
}