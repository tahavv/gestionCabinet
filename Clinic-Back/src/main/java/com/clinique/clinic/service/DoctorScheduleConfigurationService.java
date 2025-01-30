package com.clinique.clinic.service;

import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.model.DoctorRecurringSchedule;
import com.clinique.clinic.model.DoctorScheduleOverride;
import com.clinique.clinic.repositories.DoctorRecurringScheduleRepository;
import com.clinique.clinic.repositories.DoctorRepository;
import com.clinique.clinic.repositories.DoctorScheduleOverrideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class DoctorScheduleConfigurationService {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private DoctorRecurringScheduleRepository recurringRepo;
    @Autowired
    private DoctorScheduleOverrideRepository overrideRepo;

    public DoctorRecurringSchedule createOrUpdateRecurringSchedule(
            Long doctorId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    ) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorRecurringSchedule schedule = new DoctorRecurringSchedule();
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);

        return recurringRepo.save(schedule);
    }
    public List<DoctorRecurringSchedule> getRecurringSchedules(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return recurringRepo.findByDoctor(doctor);
    }

    public void deleteRecurringSchedule(Long recurringScheduleId) {
        recurringRepo.deleteById(recurringScheduleId);
    }

    public DoctorScheduleOverride createOverride(
            Long doctorId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            boolean isAvailable
    ) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorScheduleOverride ov = new DoctorScheduleOverride();
        ov.setDoctor(doctor);
        ov.setDate(date);
        ov.setStartTime(startTime);
        ov.setEndTime(endTime);
        ov.setAvailable(isAvailable);

        return overrideRepo.save(ov);
    }

    public List<DoctorScheduleOverride> getOverrides(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        // or a date range variant
        // e.g. findByDoctorAndDateBetween(doctor, fromDate, toDate)
        return overrideRepo.findByDoctorAndDateBetween(doctor, LocalDate.now().minusYears(1), LocalDate.now().plusYears(5));
    }

    public void deleteOverride(Long overrideId) {
        overrideRepo.deleteById(overrideId);
    }
}
