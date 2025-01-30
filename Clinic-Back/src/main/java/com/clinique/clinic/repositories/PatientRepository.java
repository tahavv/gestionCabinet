package com.clinique.clinic.repositories;

import com.clinique.clinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByEmail(String email);
    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(p.email) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Patient> searchByNameOrEmail(@Param("term") String term);

    Optional<Patient> findPatientByEmail(String email);
}
