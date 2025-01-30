package com.clinique.clinic.model.dto;

import com.clinique.clinic.model.enums.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.security.Timestamp;
import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    private Role role;
//    private String phoneNumber;
//    private Date dob;
}
