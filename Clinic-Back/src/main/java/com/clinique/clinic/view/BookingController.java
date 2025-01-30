package com.clinique.clinic.view;

import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.model.MedicalNote;
import com.clinique.clinic.model.MedicalRecord;
import com.clinique.clinic.model.Patient;
import com.clinique.clinic.service.AppointmentService;
import com.clinique.clinic.service.DoctorService;
import com.clinique.clinic.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/web/booking")
public class BookingController {

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private PatientService patientService;
    @GetMapping("/doctor/{doctorId}")
    public String showDoctorSlots(@PathVariable("doctorId") Long doctorId,
                                  @RequestParam(name="start", required=false) String startStr,
                                  @RequestParam(name="end", required=false) String endStr,
                                  Model model,
                                  Authentication auth) {
        Doctor doctor = doctorService.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDateTime start = (startStr != null)
                ? LocalDateTime.parse(startStr)
                : LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime end = (endStr != null)
                ? LocalDateTime.parse(endStr)
                : start.plusDays(7);
        List<LocalDateTime> availableSlots = appointmentService.findAvailableSlotsForInterval(
                doctorId, start, end
        );
        model.addAttribute("doctor", doctor);
        model.addAttribute("availableSlots", availableSlots);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        return "doctors/doctorSlots";
    }

    @PostMapping("/doctor/{doctorId}/book")
    public String bookSlot(@PathVariable("doctorId") Long doctorId,
                           @RequestParam("slot") String slotStr,
                           @RequestParam(name="cost", required=false) BigDecimal cost,
                           Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        String userEmail = auth.getName();
        Patient patient = patientService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LocalDateTime slot = LocalDateTime.parse(slotStr);

        if (cost == null) cost = BigDecimal.ZERO;

        try {
            appointmentService.createAppointment(doctorId, patient.getId(), slot, cost);
        } catch (RuntimeException ex) {
            return "redirect:/web/booking/doctor/" + doctorId + "?error=" + ex.getMessage();
        }

        return "redirect:/web/welcome";
    }
}
