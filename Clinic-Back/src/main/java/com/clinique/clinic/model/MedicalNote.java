package com.clinique.clinic.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notes")
public class MedicalNote {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String content;
    @ManyToOne
    private MedicalRecord medicalRecord;
    @ManyToOne
    private Appointment appointment;
    private LocalDateTime createdAt = LocalDateTime.now();
}
