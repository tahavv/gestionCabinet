package com.clinique.clinic.repositories;

import com.clinique.clinic.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patient.id = :patientId")
    MedicalRecord findByPatientId(@Param("patientId") Long patientId);
}
