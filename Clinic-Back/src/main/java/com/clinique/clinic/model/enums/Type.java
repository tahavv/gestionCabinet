package com.clinique.clinic.model.enums;

public enum Type {
    GENERAL_CONSULTATION("General Consultation"),
    SPECIALIST_CONSULTATION("Specialist Consultation"),
    FOLLOW_UP("Follow-Up"),
    DIAGNOSTIC_TEST("Diagnostic Test"),
    LAB_TEST("Lab Test"),
    SURGERY("Surgery"),
    VACCINATION("Vaccination"),
    EMERGENCY("Emergency"),
    TELECONSULTATION("Teleconsultation"),
    WELLNESS_CHECKUP("Wellness Check-Up");

    private final String description;

    Type(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
