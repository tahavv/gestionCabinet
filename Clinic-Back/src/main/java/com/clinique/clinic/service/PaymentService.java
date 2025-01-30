package com.clinique.clinic.service;

import com.clinique.clinic.model.Appointment;
import com.clinique.clinic.model.Payment;
import com.clinique.clinic.repositories.AppointmentRepository;
import com.clinique.clinic.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    public Payment processPayment(Long appointmentId, BigDecimal amount) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        Payment payment = new Payment();
        payment.setAppointment(appointment);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());
        // set method, e.g. payment.setMethod(PaymentMethod.credit);
        return paymentRepository.save(payment);
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }
}
