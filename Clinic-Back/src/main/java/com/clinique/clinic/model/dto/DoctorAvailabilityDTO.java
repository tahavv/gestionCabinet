package com.clinique.clinic.model.dto;

import com.clinique.clinic.model.Doctor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityDTO {
    private Doctor doctor;
    private String doctorName;
    private List<LocalDateTime> slots;
}
