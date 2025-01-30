package com.clinique.clinic.repositories;

import com.clinique.clinic.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    Optional<ResetToken> findByToken(String token);
}
