package com.clinique.clinic.view;

import com.clinique.clinic.model.Payment;
import com.clinique.clinic.service.AppointmentServiceV2;
import com.clinique.clinic.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/web/admin/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AppointmentServiceV2 appointmentService;

    @GetMapping
    public String listPayments(Model model) {
        List<Payment> payments = paymentService.findAll();
        model.addAttribute("payments", payments);
        return "payments/paymentList";
    }

    @PostMapping("/process")
    public String processPayment(@RequestParam("appointmentId") Long appointmentId,
                                 @RequestParam("amount") BigDecimal amount) {
        paymentService.processPayment(appointmentId, amount);
        return "redirect:/payments";
    }

    // Form to show possible appointment selection, etc.
    @GetMapping("/new")
    public String newPaymentForm(Model model) {
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        return "payments/paymentForm";
    }
}
