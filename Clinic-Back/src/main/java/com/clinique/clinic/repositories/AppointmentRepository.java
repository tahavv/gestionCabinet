package com.clinique.clinic.repositories;

import com.clinique.clinic.model.Appointment;
import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorAndDateDebutBetween(Doctor doctor, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByDateDebutBetween(LocalDateTime start, LocalDateTime end);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);

    // Replace this with the correct property name
    List<Appointment> findByDateDebut(LocalDateTime dateDebut);

    // Replace this with the correct property name
    List<Appointment> findByDoctorAndDateDebut(Doctor doctor, LocalDateTime dateDebut);

    // Replace this with the correct property name
    List<Appointment> findByPatientAndDateDebutAfter(Patient patient, LocalDateTime dateDebut);
}

