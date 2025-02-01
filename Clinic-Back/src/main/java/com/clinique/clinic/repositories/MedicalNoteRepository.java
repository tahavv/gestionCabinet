package com.clinique.clinic.repositories;

import com.clinique.clinic.model.MedicalNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalNoteRepository extends JpaRepository<MedicalNote,Long> {
    List<MedicalNote> findByAppointmentId(Long appointmentId);
}
