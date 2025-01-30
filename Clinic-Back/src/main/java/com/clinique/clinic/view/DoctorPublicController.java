package com.clinique.clinic.view;

import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DoctorPublicController {

    @Autowired
    private DoctorService doctorService;
    @GetMapping("/doctors-public")
    public String showAllDoctors(Model model) {
        List<Doctor> doctors = doctorService.getDoctorList();
        model.addAttribute("doctors", doctors);
        return "doctors/publicDoctorList";
    }
}
