package com.clinique.clinic.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("DOCTOR")
public class Doctor extends User {
    private String specialty;
    private String biography; // Additional information about the doctor
    private Double rating; // Average rating from patient reviews
}