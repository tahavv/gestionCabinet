package com.clinique.clinic.repositories;

import com.clinique.clinic.model.Appointment;
import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.model.Patient;
import com.clinique.clinic.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    @Query("SELECT a FROM Appointment a WHERE a.status = :status ORDER BY a.dateDebut ASC")
    List<Appointment> findByStatusOrderByDateDebutAsc(@Param("status") AppointmentStatus status);

    Optional<Appointment> findFirstByStatusOrderByDateDebutAsc(AppointmentStatus status);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.status = :status " +
            "  AND a.dateDebut >= :startOfDay " +
            "  AND a.dateDebut < :endOfDay " +
            "ORDER BY a.dateDebut ASC")
    List<Appointment> findByStatusAndDateDebutBetween(
            @Param("status") AppointmentStatus status,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    // AppointmentRepository
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :docId " +
            " AND a.dateDebut >= :start AND a.dateDebut < :end " +
            " ORDER BY a.dateDebut ASC")
    List<Appointment> findForDoctorBetween(@Param("docId") Long doctorId,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);

    // For current hour appointment
    Optional<Appointment> findFirstByDoctorIdAndDateDebutLessThanEqualAndDateFinGreaterThan(
            Long doctorId, LocalDateTime now1, LocalDateTime now2
    );

    // For next appointment
    Optional<Appointment> findFirstByDoctorIdAndStatusAndDateDebutAfterOrderByDateDebutAsc(
            Long doctorId, AppointmentStatus status, LocalDateTime now
    );

}

