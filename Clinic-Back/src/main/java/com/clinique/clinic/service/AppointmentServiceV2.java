package com.clinique.clinic.service;

import com.clinique.clinic.model.Appointment;
import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.model.Patient;
import com.clinique.clinic.model.enums.AppointmentStatus;
import com.clinique.clinic.repositories.AppointmentRepository;
import com.clinique.clinic.repositories.DoctorRepository;
import com.clinique.clinic.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceV2 {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PatientRepository patientRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment bookAppointment(Doctor doctor, Patient patient, LocalDateTime startTime, LocalDateTime endTime) {
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDateDebut(startTime);
        appointment.setDateFin(endTime);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        return appointmentRepository.save(appointment);
    }

    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }


    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void delete(Long id) {
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> findByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> findByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public boolean isDoctorAvailable(Doctor doctor, LocalDateTime date) {
        List<Appointment> sameDay = appointmentRepository.findByDoctorAndDateDebutBetween(doctor, date,LocalDateTime.now());
        return sameDay.size() < 10;
    }

    public Appointment createAppointment(Long doctorId, Long patientId, Appointment appointment) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Check availability, etc.
        if (!isDoctorAvailable(doctor, appointment.getDateDebut())) {
            throw new RuntimeException("Doctor is not available on " + appointment.getDateDebut());
        }

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        return appointmentRepository.save(appointment);
    }
}