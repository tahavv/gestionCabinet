package com.clinique.clinic.model;

import com.clinique.clinic.model.enums.AppointmentStatus;
import com.clinique.clinic.model.enums.Type;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @ManyToOne
    @NotNull(message = "Patient is required")
    private Patient patient;

    @NotNull(message = "Start date and time are required")
    private LocalDateTime dateDebut;

    @NotNull(message = "End date and time are required")
    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    private boolean paid;

    private BigDecimal cost;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    private String reason;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalNote> notes = new ArrayList<>();
}