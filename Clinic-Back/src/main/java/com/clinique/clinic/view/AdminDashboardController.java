package com.clinique.clinic.view;

import com.clinique.clinic.service.DoctorAvailabilityService;
import com.clinique.clinic.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/web/admin")
public class AdminDashboardController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorAvailabilityService doctorAvailabilityService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "redirect:/web/admin/overview";
    }

    @GetMapping("/overview")
    public String overview(Model model) {
        model.addAttribute("someStat", 42);
        model.addAttribute("content", "admin/overview");
        return "admin/dashboard";
    }

    @GetMapping("/analytics/dashboard")
    public String analytics(Model model) {
        // Add data for the analytics page
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