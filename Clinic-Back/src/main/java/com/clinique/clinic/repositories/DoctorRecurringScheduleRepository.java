package com.clinique.clinic.repositories;

import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.model.DoctorRecurringSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface DoctorRecurringScheduleRepository extends JpaRepository<DoctorRecurringSchedule, Long> {
    List<DoctorRecurringSchedule> findByDoctor(Doctor doctor);
    List<DoctorRecurringSchedule> findDoctorRecurringScheduleByDoctorId(Long findByDoctorId);
    List<DoctorRecurringSchedule> findByDoctorAndDayOfWeek(Doctor doctor, DayOfWeek dayOfWeek);
}

