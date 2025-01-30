package com.clinique.clinic.view;

import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.model.dto.Slide;
import com.clinique.clinic.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/welcome")
public class DasboardWebController {
    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public String showDashboard(
            @RequestParam(value="searchTerm", required=false) String searchTerm,
            Model model
    ) {
        List<Doctor> doctors;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            doctors = doctorService.searchByNameOrSpecialty(searchTerm.trim());
        } else {
            doctors = doctorService.getDoctorList();
        }
        Map<String, List<Doctor>> specialtyMap = doctors.stream()
                .collect(Collectors.groupingBy(Doctor::getSpecialty));
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("specialtyMap", specialtyMap);

        return "layouts/welcome";
    }



    @GetMapping("/carousel-demo")
    public String carouselDemo(Model model) {
        return "fragments/carousel";
    }


    public List<Slide> getSlides() {
        List<Slide> slides = new ArrayList<>();
        slides.add(new Slide("State-of-the-Art Facilities", "Experience healthcare in our modern medical center", "/images/Healthcare1.jpg"));
        slides.add(new Slide("Advanced Technology", "Using the latest medical technology for better treatment", "/images/Healthcare2.jpeg"));
        slides.add(new Slide("Expert Medical Team", "Our experienced doctors provide the best care", "/images/Healthcare3.jpg"));
        return slides;
    }
}
