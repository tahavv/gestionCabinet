package com.clinique.clinic.view;

import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/web/admin/doctors")
public class AdminDoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/list")
    public String listDoctors(
            @RequestParam(name="searchTerm", required=false) String searchTerm,
            Model model
    ) {
        List<Doctor> doctors;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            doctors = doctorService.searchByNameOrSpecialty(searchTerm.trim());
        } else {
            doctors = doctorService.getDoctorList();
        }
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("doctors", doctors);
        model.addAttribute("content", "doctors/doctorList");
        return "admin/dashboard";
    }

    @GetMapping("/new")
    public String newDoctor(Model model) {
        model.addAttribute("doctor", new Doctor());
        model.addAttribute("content", "doctors/newDoctorForm");
        return "admin/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String editDoctor(@PathVariable("id") Long id, Model model) {
        Doctor doctor = doctorService.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        model.addAttribute("doctor", doctor);
        model.addAttribute("content", "doctors/newDoctorForm");
        return "admin/dashboard";
    }

    @PostMapping("/save")
    public String saveDoctor(@ModelAttribute Doctor doctor) {
        doctorService.saveWithDefaultSchedule(doctor);
        return "redirect:/web/admin/doctors/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteDoctor(@PathVariable("id") Long id) {
        doctorService.delete(id);
        return "redirect:/web/admin/doctors/list";
    }

    @GetMapping("/schedule")
    public String schedule(Model model) {
        model.addAttribute("content", "doctors/schedule");
        return "admin/dashboard";
    }

    @GetMapping("/availability")
    public String availability(Model model) {
        model.addAttribute("doctors", doctorService.getDoctorList());
        model.addAttribute("content", "doctors/availability");
        return "admin/dashboard";
    }
}
