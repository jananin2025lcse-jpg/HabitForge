package com.example.habitforge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;

@Entity
@Table(name = "completion_logs", uniqueConstraints = @UniqueConstraint(columnNames = {"habit_id", "completion_date"}))
public class CompletionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long completionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    private LocalDate completionDate;

    @Enumerated(EnumType.STRING)
    private CompletionStatus status;

    public Long getCompletionId() { return completionId; }
    public void setCompletionId(Long completionId) { this.completionId = completionId; }
    public Habit getHabit() { return habit; }
    public void setHabit(Habit habit) { this.habit = habit; }
    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }
    public CompletionStatus getStatus() { return status; }
    public void setStatus(CompletionStatus status) { this.status = status; }
}