package com.clinique.clinic.controller;

import com.clinique.clinic.model.DoctorRecurringSchedule;
import com.clinique.clinic.model.DoctorScheduleOverride;
import com.clinique.clinic.service.DoctorScheduleConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/doctor-schedule")
@Tag(name = "doctor schedule Controller", description = "API for managing doctor schedule")
public class DoctorScheduleConfigurationController {

    @Autowired
    private DoctorScheduleConfigurationService scheduleConfigService;

    // 1) Create or update a recurring schedule row
    @PostMapping("/recurring")
    public DoctorRecurringSchedule createRecurring(
            @RequestParam Long doctorId,
            @RequestParam DayOfWeek dayOfWeek,
            @RequestParam String startTime,
            @RequestParam String endTime
    ) {
        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);
        return scheduleConfigService.createOrUpdateRecurringSchedule(
                doctorId, dayOfWeek, st, et
        );
    }

    // 2) Get all recurring schedules for a doctor
    @GetMapping("/recurring")
    public List<DoctorRecurringSchedule> getRecurring(@RequestParam Long doctorId) {
        return scheduleConfigService.getRecurringSchedules(doctorId);
    }

    // 3) Delete a recurring schedule row by ID
    @DeleteMapping("/recurring/{id}")
    public void deleteRecurring(@PathVariable Long id) {
        scheduleConfigService.deleteRecurringSchedule(id);
    }

    // 4) Create an override
    // You can do it with a POST request body or request params (like below).
    @PostMapping("/override")
    public DoctorScheduleOverride createOverride(
            @RequestParam Long doctorId,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam boolean isAvailable
    ) {
        LocalTime st = LocalTime.parse(startTime);
        LocalTime et = LocalTime.parse(endTime);
        LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        return scheduleConfigService.createOverride(
                doctorId, parsedDate, st, et, isAvailable
        );
    }

    // 5) Get all overrides for a doctor
    @GetMapping("/override")
    public List<DoctorScheduleOverride> getOverrides(@RequestParam Long doctorId) {
        return scheduleConfigService.getOverrides(doctorId);
    }

    // 6) Delete an override
    @DeleteMapping("/override/{id}")
    public void deleteOverride(@PathVariable Long id) {
        scheduleConfigService.deleteOverride(id);
    }
}
