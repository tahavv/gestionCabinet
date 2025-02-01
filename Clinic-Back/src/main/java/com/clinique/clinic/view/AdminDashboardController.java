package com.clinique.clinic.view;

import com.clinique.clinic.model.Appointment;
import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.model.MedicalNote;
import com.clinique.clinic.model.Patient;
import com.clinique.clinic.service.AppointmentService;
import com.clinique.clinic.service.AppointmentServiceV2;
import com.clinique.clinic.service.DoctorAvailabilityService;
import com.clinique.clinic.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/web/admin")
public class AdminDashboardController {

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DoctorAvailabilityService doctorAvailabilityService;
    @Autowired
    private AppointmentServiceV2 appointmentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "redirect:/web/admin/overview";
    }

    @GetMapping("/overview")
    public String overview(Model model, Authentication auth) {
        Doctor loggedDoctor = getLoggedDoctor(auth);
        String chartData = "someDataHere (JSON or list)";
        model.addAttribute("chartData", chartData);
        model.addAttribute("today", LocalDate.now());

        List<Appointment> todaysAppointments = appointmentService.findTodaysAppointmentsForDoctor(loggedDoctor.getId());
        model.addAttribute("todaysAppointments", todaysAppointments);

        Appointment currentHourAppt = appointmentService.findCurrentHourAppointment(loggedDoctor.getId());
        Appointment nextAppointment = appointmentService.findNextAppointment(loggedDoctor.getId());
        Appointment currentAppointment = (currentHourAppt != null) ? currentHourAppt : nextAppointment;

        model.addAttribute("currentHourAppt", currentHourAppt);
        model.addAttribute("nextAppointment", nextAppointment);
        model.addAttribute("totalToday", todaysAppointments.size());
        model.addAttribute("currentAppointment", currentAppointment);

        if (currentAppointment != null) {
            Patient patient = currentAppointment.getPatient();
            model.addAttribute("patient", patient);

            List<MedicalNote> notes = appointmentService.getNotesForAppointment(currentAppointment.getId());
            model.addAttribute("notes", notes);
        }
        model.addAttribute("content", "admin/overview");
        return "admin/dashboard";
    }

    private Doctor getLoggedDoctor(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Not authenticated");
        }
        String email = auth.getName();
        return doctorService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found for email: " + email));
    }

    @PostMapping("/overview/change-status")
    public String changeAppointmentStatus(
            @RequestParam Long appointmentId,
            @RequestParam String newStatus
    ) {
        appointmentService.changeStatus(appointmentId, newStatus);
        return "redirect:/web/admin/overview";
    }

    @PostMapping("/overview/add-note")
    public String addAppointmentNote(
            @RequestParam Long appointmentId,
            @RequestParam String noteContent
    ) {
        appointmentService.addNoteToAppointment(appointmentId, noteContent);
        return "redirect:/web/admin/overview";
    }

    @PostMapping("/overview/next-patient")
    public String goToNextPatient() {
        appointmentService.advanceToNextPatient();
        return "redirect:/web/admin/overview";
    }


    @GetMapping("/analytics/dashboard")
    public String analytics(Model model) {
        model.addAttribute("chartData", "someDataHere");
        model.addAttribute("content", "admin/analytics");
        return "admin/dashboard";
    }

    @GetMapping("/doctors/availability/dashboard-view")
    public String doctorAvailability(Model model) {
        model.addAttribute("doctors", doctorService.getDoctorList());
        model.addAttribute("content", "doctors/availability");
        return "admin/dashboard";
    }

    @GetMapping("/doctors/availability/manage")
    public String manageAvailability(Model model) {
        model.addAttribute("doctors", doctorService.getDoctorList());
        return "admin/doctor-availability";
    }

    @PostMapping("/doctors/availability/recurring")
    public String addRecurringSchedule(@RequestParam Long doctorId, @RequestParam DayOfWeek dayOfWeek,
                                       @RequestParam LocalTime startTime, @RequestParam LocalTime endTime) {
        doctorAvailabilityService.addRecurringSchedule(doctorId, dayOfWeek, startTime, endTime);
        return "redirect:/web/admin/doctors/availability";
    }

    @PostMapping("/doctors/availability/override")
    public String addScheduleOverride(@RequestParam Long doctorId, @RequestParam LocalDate date,
                                      @RequestParam LocalTime startTime, @RequestParam LocalTime endTime,
                                      @RequestParam boolean isAvailable) {
        doctorAvailabilityService.addScheduleOverride(doctorId, date, startTime, endTime, isAvailable);
        return "redirect:/web/admin/doctors/availability";
    }
}