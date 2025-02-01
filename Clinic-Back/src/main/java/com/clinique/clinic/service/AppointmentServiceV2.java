package com.clinique.clinic.service;

import com.clinique.clinic.model.*;
import com.clinique.clinic.model.enums.AppointmentStatus;
import com.clinique.clinic.repositories.AppointmentRepository;
import com.clinique.clinic.repositories.DoctorRepository;
import com.clinique.clinic.repositories.MedicalNoteRepository;
import com.clinique.clinic.repositories.MedicalRecordRepository;
import com.clinique.clinic.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    @Autowired
    private MedicalNoteRepository medicalNoteRepository;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    public Appointment bookAppointment(Doctor doctor, Patient patient,
                                       LocalDateTime startTime, LocalDateTime endTime) {
        Appointment appt = new Appointment();
        appt.setDoctor(doctor);
        appt.setPatient(patient);
        appt.setDateDebut(startTime);
        appt.setDateFin(endTime);
        appt.setStatus(AppointmentStatus.SCHEDULED);
        return appointmentRepository.save(appt);
    }

    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
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
        List<Appointment> sameDay = appointmentRepository
                .findByDoctorAndDateDebutBetween(doctor, date, LocalDateTime.now());
        return sameDay.size() < 10;
    }
    public Appointment createAppointment(Long doctorId, Long patientId, Appointment appointment) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        if (!isDoctorAvailable(doctor, appointment.getDateDebut())) {
            throw new RuntimeException("Doctor is not available on " + appointment.getDateDebut());
        }

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        return appointmentRepository.save(appointment);
    }

    public Appointment getCurrentAppointment() {
        Optional<Appointment> apptOpt = appointmentRepository
                .findFirstByStatusOrderByDateDebutAsc(AppointmentStatus.SCHEDULED);
        return apptOpt.orElse(null);
    }

    public List<Appointment> getTodaysAppointments() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return appointmentRepository.findByDateDebutBetween(startOfDay, endOfDay);
    }



    public List<MedicalNote> getNotesForAppointment(Long appointmentId) {
        return medicalNoteRepository.findByAppointmentId(appointmentId);
    }

    public void changeStatus(Long appointmentId, String newStatusStr) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        AppointmentStatus newStatus = AppointmentStatus.valueOf(newStatusStr.toUpperCase());
        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);
    }

    public void addNoteToAppointment(Long appointmentId, String noteContent) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        MedicalNote note = new MedicalNote();
        note.setAppointment(appointment);
        note.setCreatedAt(LocalDateTime.now());
        note.setContent(noteContent);
        Patient patient = appointment.getPatient();
        if (patient != null) {
            // find or create a MedicalRecord for that patient
            MedicalRecord record = medicalRecordRepository.findByPatientId(patient.getId());
            if (record == null) {
                record = new MedicalRecord();
                record.setPatient(patient);
                // record.setNotes(new ArrayList<>()); // if needed
                medicalRecordRepository.save(record);
            }
            note.setMedicalRecord(record);
        }

        medicalNoteRepository.save(note);

        // If you want the note also in appointment.getNotes() or record.getNotes(), you could do:
        // appointment.getNotes().add(note);
        // appointmentRepository.save(appointment);
        // or record.getNotes().add(note);
        // medicalRecordRepository.save(record);
    }

    public void advanceToNextPatient() {
        Appointment current = getCurrentAppointment();
        if (current != null) {
            current.setStatus(AppointmentStatus.COMPLETED);
            appointmentRepository.save(current);
        }
    }

    public List<Appointment> findTodaysScheduledAppointments() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay   = startOfDay.plusDays(1);

        return appointmentRepository.findByStatusAndDateDebutBetween(
                AppointmentStatus.SCHEDULED, startOfDay, endOfDay
        );
    }

    public List<Appointment> findTodaysAppointmentsForDoctor(Long doctorId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return appointmentRepository.findForDoctorBetween(doctorId, startOfDay, endOfDay);
    }

    public Appointment findCurrentHourAppointment(Long doctorId) {
        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository.findFirstByDoctorIdAndDateDebutLessThanEqualAndDateFinGreaterThan(doctorId, now, now)
                .orElse(null);
    }

    public Appointment findNextAppointment(Long doctorId) {
        LocalDateTime now = LocalDateTime.now();
        return appointmentRepository.findFirstByDoctorIdAndStatusAndDateDebutAfterOrderByDateDebutAsc(doctorId, AppointmentStatus.SCHEDULED, now)
                .orElse(null);
    }


}
