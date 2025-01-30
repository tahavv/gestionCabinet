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
    public String listDoctors(Model model) {
        List<Doctor> doctors = doctorService.getDoctorList(); // or .findAll()
        model.addAttribute("doctors", doctors);
        model.addAttribute("content", "doctors/doctorList");
        return "admin/dashboard";
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

    @GetMapping("/new")
    public String newDoctor(Model model) {
        model.addAttribute("doctor", new Doctor());
        model.addAttribute("content", "doctors/newDoctorForm");
        return "admin/dashboard";
    }

    @PostMapping("/save")
    public String saveDoctor(@ModelAttribute Doctor doctor) {
        doctorService.save(doctor);
        return "redirect:/web/admin/doctors/list";
    }
}
