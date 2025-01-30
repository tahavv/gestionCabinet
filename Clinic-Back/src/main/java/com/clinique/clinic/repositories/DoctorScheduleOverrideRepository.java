package com.clinique.clinic.repositories;

import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.model.DoctorScheduleOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorScheduleOverrideRepository extends JpaRepository<DoctorScheduleOverride, Long> {
    List<DoctorScheduleOverride> findByDoctorAndDateBetween(Doctor doctor, LocalDate start, LocalDate end);
    List<DoctorScheduleOverride> findDoctorScheduleOverrideByDoctorId(Long findByDoctorId);

    // Or just findByDoctorAndDate(Doctor doctor, LocalDate date) if you prefer daily queries
}

