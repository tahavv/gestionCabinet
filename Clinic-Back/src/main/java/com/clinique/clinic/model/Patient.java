package com.clinique.clinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("PATIENT")
public class Patient extends User {
    private String insuranceNumber;
    private String preferredCommunicationMethod;
}