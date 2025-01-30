package com.clinique.clinic.service;

import com.clinique.clinic.model.MedicalNote;
import com.clinique.clinic.model.MedicalRecord;
import com.clinique.clinic.repositories.MedicalRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public List<MedicalRecord> findAll() {
        return medicalRecordRepository.findAll();
    }

    public Optional<MedicalRecord> findById(Long id) {
        return medicalRecordRepository.findById(id);
    }
    @Transactional
    public MedicalRecord save(MedicalRecord record) {
        return medicalRecordRepository.save(record);
    }
    @Transactional
    public void addNote(MedicalRecord record, MedicalNote note) {
        record.getNotes().add(note);
        note.setMedicalRecord(record);
        medicalRecordRepository.save(record);
    }

    @Transactional()
    public MedicalRecord findByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }

}
