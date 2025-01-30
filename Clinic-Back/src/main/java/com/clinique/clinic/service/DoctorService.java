package com.clinique.clinic.service;

import com.clinique.clinic.model.Doctor;
import com.clinique.clinic.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

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
}
