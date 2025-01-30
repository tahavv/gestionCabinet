package com.clinique.clinic.service;
import com.clinique.clinic.model.Patient;
import com.clinique.clinic.model.User;
import com.clinique.clinic.model.dto.SignupRequestDTO;
import com.clinique.clinic.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    // Create a User
    public User createUser(SignupRequestDTO userDTO)  {
        Patient user = new Patient();
        user.setFullName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getName()));
        //user.setDob(userDTO.getDob());
        user.setRole(userDTO.getRole());
        try {
            emailService.sendWelcomeEmail(userDTO.getEmail(),userDTO.getName(),userDTO.getPassword());
            user.setVerified(true);
        }catch (Exception e){
            System.out.println("Error sending welcome email :"+e.getMessage());
            user.setVerified(false);
        }
        return userRepository.save(user);
    }

    // Get all Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get User by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Update a User
    public Optional<User> updateUser(Long id, User userDTO) {
        return userRepository.findById(id)
                .map(user -> {
                    if (userDTO.getFullName() != null) {
                        user.setFullName(userDTO.getFullName());
                    }
                    if (userDTO.getEmail() != null) {
                        user.setEmail(userDTO.getEmail());
                    }
                    if (userDTO.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                    }
                    if (userDTO.getDob() != null) {
                        user.setDob(userDTO.getDob());
                    }
                    if(userDTO.getRole() != null) {
                        user.setRole(userDTO.getRole());
                    }
                    //user.setAccountLocked(userDTO.isAccountLocked());
                    return userRepository.save(user);
                });
    }

    // Delete a User
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
