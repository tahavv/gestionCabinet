package com.clinique.clinic.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/admin/settings")
public class AdminSettingsController {

    @GetMapping("/general")
    public String generalSettings(Model model) {
        model.addAttribute("content", "settings/general");
        return "admin/dashboard";
    }

    @GetMapping("/security")
    public String securitySettings(Model model) {
        model.addAttribute("content", "settings/security");
        return "admin/dashboard";
    }
}
