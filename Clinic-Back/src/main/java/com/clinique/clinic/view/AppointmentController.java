package com.clinique.clinic.view;

import com.clinique.clinic.model.Appointment;
import com.clinique.clinic.model.enums.AppointmentStatus;
import com.clinique.clinic.service.AppointmentServiceV2;
import com.clinique.clinic.service.DoctorService;
import com.clinique.clinic.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.clinique.clinic.model.enums.Type;

import java.util.List;

@Controller
@RequestMapping("/web/appointments")
public class AppointmentController {
    @Autowired
    private AppointmentServiceV2 appointmentService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;

    @GetMapping
    public String listAppointments(Model model) {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        model.addAttribute("appointments", appointments);
        return "appointments/appointmentList";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("doctors", doctorService.getDoctorList());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("types", Type.values());
        return "appointments/appointmentForm";
    }

    @PostMapping
    public String createAppointment(
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("patientId") Long patientId,
            @ModelAttribute Appointment appointment
    ) {
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentService.createAppointment(doctorId, patientId, appointment);
        return "redirect:/appointments";
    }

    @GetMapping("/edit/{id}")
    public String editAppointment(@PathVariable("id") Long id, Model model) {
        Appointment appointment = appointmentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        model.addAttribute("appointment", appointment);
        model.addAttribute("doctors", doctorService.getDoctorList());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("types", Type.values());
        return "appointments/appointmentForm";
    }

    @PostMapping("/update/{id}")
    public String updateAppointment(@PathVariable("id") Long id,
                                    @ModelAttribute Appointment appointment,
                                    @RequestParam("doctorId") Long doctorId,
                                    @RequestParam("patientId") Long patientId) {
        // load original
        Appointment existing = appointmentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        existing.setDoctor(doctorService.findById(doctorId).orElse(null));
        existing.setPatient(patientService.findById(patientId).orElse(null));
        existing.setDateDebut(appointment.getDateDebut());
        existing.setDateFin(appointment.getDateFin());
        existing.setStatus(appointment.getStatus());
        existing.setType(appointment.getType());
//        existing.setNotes(appointment.getNotes());
        appointmentService.save(existing);
        return "redirect:/appointments";
    }

    @GetMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable("id") Long id) {
        Appointment appointment = appointmentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentService.save(appointment);
        return "redirect:/appointments";
    }

    @GetMapping("/complete/{id}")
    public String completeAppointment(@PathVariable("id") Long id) {
        Appointment appointment = appointmentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentService.save(appointment);
        return "redirect:/appointments";
    }
}

