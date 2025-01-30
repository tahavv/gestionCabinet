package com.clinique.clinic.controller;

import com.clinique.clinic.model.dto.DoctorAvailabilityDTO;
import com.clinique.clinic.service.AppointmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController("appointmentWelcome")
@RequestMapping("/api/appointments")
@Tag(name = "Appointments Controller", description = "API for managing Appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/available-all")
    public ResponseEntity<?> getAllDoctorsAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<DoctorAvailabilityDTO> availability =
                appointmentService.findAllAvailableSlotsForAllDoctors(start, end);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/available-by-specialty")
    public ResponseEntity<?> getDoctorsAvailabilityBySpecialty(
            @RequestParam String specialty,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<DoctorAvailabilityDTO> availability =
                appointmentService.findAllAvailableSlotsBySpecialty(specialty, start, end);
        return ResponseEntity.ok(availability);
    }

}
