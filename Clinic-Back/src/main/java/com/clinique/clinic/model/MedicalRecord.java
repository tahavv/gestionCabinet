package com.clinique.clinic.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Patient patient;

    @OneToMany(mappedBy="medicalRecord", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<MedicalNote> notes = new ArrayList<>();

    public void addNote(MedicalNote note) {
        note.setMedicalRecord(this);
        this.notes.add(note);
    }
}