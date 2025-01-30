package com.clinique.clinic.view;

import com.clinique.clinic.model.dto.DoctorAvailabilityDTO;
import com.clinique.clinic.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/web/appointments")
public class AppointmentWebController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/available-all")
    public String getAllDoctorsAvailability(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Model model
    ) {
        if (start == null || end == null) {
            LocalDateTime now = LocalDateTime.now();
            start = now.minusWeeks(6);
            end   = now.plusWeeks(6);
        }

        List<DoctorAvailabilityDTO> availability =
                appointmentService.findAllAvailableSlotsForAllDoctors(start, end);
        model.addAttribute("availabilityList", availability);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);

        return "fragments/available";
    }

    @GetMapping("/available-by-specialty")
    public String getDoctorsAvailabilityBySpecialty(
            @RequestParam String specialty,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Model model
    ) {
        if (start == null || end == null) {
            LocalDateTime now = LocalDateTime.now();
            start = now.minusWeeks(6);
            end   = now.plusWeeks(6);
        }

        List<DoctorAvailabilityDTO> availability =
                appointmentService.findAllAvailableSlotsBySpecialty(specialty, start, end);

        model.addAttribute("availabilityList", availability);
        model.addAttribute("specialty", specialty);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);

        return "fragments/available-specialty";}

}
