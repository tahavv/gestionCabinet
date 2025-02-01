package com.clinique.clinic.service;

import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.model.DoctorRecurringSchedule;
import com.clinique.clinic.model.enums.Role;
import com.clinique.clinic.repositories.DoctorRecurringScheduleRepository;
import com.clinique.clinic.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private DoctorRecurringScheduleRepository recurringScheduleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Doctor> getDoctorList(){
        return doctorRepository.findAll();
    }

    public Optional<Doctor> findById(Long id) {
        return doctorRepository.findById(id);
    }

    public Doctor save(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public void delete(Long id) {
        doctorRepository.deleteById(id);
    }

    public List<Doctor> searchByNameOrSpecialty(String term) {
        return doctorRepository.findAll().stream()
                .filter(doc -> doc.getFullName().toLowerCase().contains(term.toLowerCase())
                        || (doc.getSpecialty() != null && doc.getSpecialty().toLowerCase().contains(term.toLowerCase())))
                .collect(Collectors.toList());
    }

    public Doctor saveWithDefaultSchedule(Doctor doctor) {
        boolean isNew = (doctor.getId() == null);
        if (isNew || isPasswordChanged(doctor)) {
            doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        }
        if(doctor.getSpecialty().isEmpty()){
            doctor.setSpecialty("Genraliste");
        }
        doctor.setRole(Role.DOCTOR);
        doctor.setVerified(true);
        Doctor saved = doctorRepository.save(doctor);

        if (isNew) {
            createDefaultRecurringSchedule(saved);
        }

        return saved;
    }

    private boolean isPasswordChanged(Doctor doctor) {
        if (doctor.getId() == null) {
            return true;
        }
        Doctor existing = doctorRepository.findById(doctor.getId()).orElse(null);
        if (existing == null) return true;
        return !existing.getPassword().equals(doctor.getPassword());
    }

    private void createDefaultRecurringSchedule(Doctor doctor) {
        List<DayOfWeek> weekdays = Arrays.asList(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY
        );
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime   = LocalTime.of(18, 0);

        for (DayOfWeek day : weekdays) {
            DoctorRecurringSchedule schedule = new DoctorRecurringSchedule();
            schedule.setDoctor(doctor);
            schedule.setDayOfWeek(day);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            recurringScheduleRepository.save(schedule);
        }
    }

    public Optional<Doctor> findByEmail(String email) {
        return doctorRepository.findDoctorByEmail(email);
    }
}
