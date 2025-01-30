package com.clinique.clinic.model.dto;

import com.clinique.clinic.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthResponseDTO {
    private String message;
    private String token;
    private User user;
}
