package com.clinique.clinic.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/admin")
public class AdminAnalyticsController {

    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("chartData", "someDataHere");
        model.addAttribute("content", "admin/analytics");
        return "admin/dashboard";
    }
}
