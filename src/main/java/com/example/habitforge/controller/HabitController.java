package com.example.habitforge.controller;

import com.example.habitforge.dto.CalendarEntryResponse;
import com.example.habitforge.dto.CompletionRequest;
import com.example.habitforge.dto.ReminderResponse;
import com.example.habitforge.dto.StreakResponse;
import com.example.habitforge.exception.BadRequestException;
import com.example.habitforge.model.CompletionLog;
import com.example.habitforge.model.Habit;
import com.example.habitforge.service.CalendarService;
import com.example.habitforge.service.CompletionService;
import com.example.habitforge.service.HabitService;
import com.example.habitforge.service.ReminderService;
import com.example.habitforge.service.StreakService;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/habits")
public class HabitController {
    private final HabitService habitService;
    private final CompletionService completionService;
    private final StreakService streakService;
    private final CalendarService calendarService;
    private final ReminderService reminderService;

    public HabitController(HabitService habitService, CompletionService completionService,
                           StreakService streakService, CalendarService calendarService,
                           ReminderService reminderService) {
        this.habitService = habitService;
        this.completionService = completionService;
        this.streakService = streakService;
        this.calendarService = calendarService;
        this.reminderService = reminderService;
    }

    @PostMapping
    public ResponseEntity<Habit> create(@Valid @RequestBody Habit habit) {
        Habit saved = habitService.create(habit);
        return ResponseEntity.created(URI.create("/api/habits/" + saved.getHabitId())).body(saved);
    }

    @GetMapping
    public List<Habit> findAll() { return habitService.findAll(); }

    @GetMapping("/{id}")
    public Habit findOne(@PathVariable Long id) { return habitService.findById(id); }

    @PutMapping("/{id}")
    public Habit update(@PathVariable Long id, @Valid @RequestBody Habit habit) {
        return habitService.update(id, habit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        habitService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public CompletionLog complete(@PathVariable Long id, @Valid @RequestBody CompletionRequest request) {
        return completionService.complete(id, request);
    }

    @GetMapping("/{id}/streak")
    public StreakResponse streak(@PathVariable Long id) { return streakService.getStreak(id); }

    @GetMapping("/{id}/calendar")
    public List<CalendarEntryResponse> calendar(@PathVariable Long id, @RequestParam String month) {
        try {
            return calendarService.getCalendar(id, YearMonth.parse(month));
        } catch (DateTimeParseException exception) {
            throw new BadRequestException("Month must use the yyyy-MM format");
        }
    }

    @GetMapping("/{id}/reminder")
    public ReminderResponse reminder(@PathVariable Long id) { return reminderService.getReminder(id); }
}