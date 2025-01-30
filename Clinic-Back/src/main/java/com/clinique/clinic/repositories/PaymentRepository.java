package com.clinique.clinic.repositories;

import com.clinique.clinic.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
