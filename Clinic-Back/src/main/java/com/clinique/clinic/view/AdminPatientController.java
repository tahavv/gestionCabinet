package com.clinique.clinic.view;

import com.clinique.clinic.model.Appointment;
import com.clinique.clinic.model.MedicalNote;
import com.clinique.clinic.model.MedicalRecord;
import com.clinique.clinic.model.Patient;
import com.clinique.clinic.model.enums.Role;
import com.clinique.clinic.service.AppointmentService;
import com.clinique.clinic.service.EmailService;
import com.clinique.clinic.service.MedicalRecordService;
import com.clinique.clinic.service.PatientService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/web/admin/patients")
public class AdminPatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private EmailService emailService;
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/list")
    public String listPatients(Model model) {
        List<Patient> patients = patientService.findAll();
        model.addAttribute("patients", patients);
        model.addAttribute("content", "patients/patientList");
        return "admin/dashboard";
    }

    @GetMapping("/list-cards")
    public String listPatientsCards(@RequestParam(name = "searchTerm", required = false) String searchTerm,
                                    Model model) {
        List<Patient> patients;
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            patients = patientService.findAll();
        } else {
            patients = patientService.searchByNameOrEmail(searchTerm.trim());
        }
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("patients", patients);
        model.addAttribute("content", "patients/patientCardList");
        return "admin/dashboard";
    }



    @GetMapping("/new")
    public String newPatient(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("initialNote", "");
        model.addAttribute("content", "patients/newPatientForm");
        return "admin/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String editPatient(@PathVariable Long id, Model model) {
        Patient patient = patientService.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        model.addAttribute("patient", patient);
        model.addAttribute("initialNote", "");
        model.addAttribute("content", "patients/newPatientForm");
        return "admin/dashboard";
    }

    @PostMapping("/save")
    public String savePatient(
            @ModelAttribute("patient") Patient patient,
            @RequestParam(name = "initialNote", required = false) String initialNote
    ) {
        String password = patient.getPassword();
        boolean isNew = (patient.getId() == null);
        patient.setVerified(true);
        patient.setRole(Role.PATIENT);
        Patient savedPatient = patientService.save(patient);
        if (isNew && initialNote != null && !initialNote.trim().isEmpty()) {
            MedicalRecord record = new MedicalRecord();
            record.setPatient(savedPatient);

            MedicalNote note = new MedicalNote();
            note.setContent(initialNote);
            note.setMedicalRecord(record);
            record.getNotes().add(note);

            medicalRecordService.save(record);
            try {
                emailService.sendWelcomeEmail(patient.getEmail(),patient.getFullName(),password);
            }catch (Exception e){
                System.out.println("Error sending welcome email :"+e.getMessage());
            }
        }
        return "redirect:/web/admin/patients/list";
    }

    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable Long id) {
        patientService.deleteById(id);
        return "redirect:/web/admin/patients/list-cards";
    }

    @GetMapping("/{id}/medical-records")
    @ResponseBody
    public MedicalRecord getMedicalRecords(@PathVariable Long id) {
        return medicalRecordService.findByPatientId(id);
    }

    @GetMapping("/export")
    public void exportPatients(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"patients.csv\"");

        List<Patient> patients = patientService.findAll();
        PrintWriter writer = response.getWriter();
        writer.println("ID,Full Name,Email,Phone,Insurance");
        for (Patient p : patients) {
            writer.printf("%d,%s,%s,%s,%s\n",
                    p.getId(), p.getFullName(), p.getEmail(), p.getPhoneNumber(), p.getInsuranceNumber());
        }
        writer.flush();
        writer.close();
    }

    @GetMapping("/{id}/records-ui")
    public String viewPatientRecords(@PathVariable Long id, Model model) {
        Patient patient = patientService.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        MedicalRecord record = medicalRecordService.findByPatientId(id);

        model.addAttribute("patient", patient);
        model.addAttribute("record", record);
        List<Appointment> appointments = appointmentService.findByPatient(id);
        model.addAttribute("appointments", appointments);
        model.addAttribute("content", "patients/medicalRecordDetail");
        return "admin/dashboard";
    }

}
