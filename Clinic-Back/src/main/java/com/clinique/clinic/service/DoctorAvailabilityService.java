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
public class DoctorAvailabilityService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorRecurringScheduleRepository recurringScheduleRepository;

    @Autowired
    private DoctorScheduleOverrideRepository overrideRepository;

    public void addRecurringSchedule(Long doctorId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));
        DoctorRecurringSchedule schedule = new DoctorRecurringSchedule();
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        recurringScheduleRepository.save(schedule);
    }

    public void addScheduleOverride(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime, boolean isAvailable) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));
        DoctorScheduleOverride override = new DoctorScheduleOverride();
        override.setDoctor(doctor);
        override.setDate(date);
        override.setStartTime(startTime);
        override.setEndTime(endTime);
        override.setAvailable(isAvailable);
        overrideRepository.save(override);
    }

    public List<DoctorRecurringSchedule> getRecurringSchedules(Long doctorId) {
        return recurringScheduleRepository.findDoctorRecurringScheduleByDoctorId(doctorId);
    }

    public List<DoctorScheduleOverride> getScheduleOverrides(Long doctorId) {
        return overrideRepository.findDoctorScheduleOverrideByDoctorId(doctorId);
    }
}