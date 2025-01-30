package com.clinique.clinic.service;

import com.clinique.clinic.model.Patient;
import com.clinique.clinic.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }


    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> findByEmail(String email) {
        return patientRepository.findPatientByEmail(email);
    }

    public Patient save(Patient patient) {
        if (patient.getId() == null) {
            patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        } else {
            Optional<Patient> existingOpt = patientRepository.findById(patient.getId());
            if (existingOpt.isPresent()) {
                String oldEncoded = existingOpt.get().getPassword();
                if (!oldEncoded.equals(patient.getPassword())) {
                    patient.setPassword(passwordEncoder.encode(patient.getPassword()));
                }
            }
        }
        return patientRepository.save(patient);
    }

    public void deleteById(Long id) {
        patientRepository.deleteById(id);
    }

    public List<Patient> searchByNameOrEmail(String term) {
        return patientRepository.searchByNameOrEmail(term);
    }

}
