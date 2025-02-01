package com.clinique.clinic.controller;

import com.clinique.clinic.model.*;
import com.clinique.clinic.model.dto.AuthResponseDTO;
import com.clinique.clinic.model.dto.ResetPasswordRequest;
import com.clinique.clinic.model.dto.SigninRequestDTO;
import com.clinique.clinic.model.dto.SignupRequestDTO;
import com.clinique.clinic.model.enums.Role;
import com.clinique.clinic.repositories.ResetTokenRepository;
import com.clinique.clinic.repositories.UserRepository;
import com.clinique.clinic.repositories.VerificationTokenRepository;
import com.clinique.clinic.service.EmailService;
import com.clinique.clinic.utils.JwtTokenUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @PostMapping("/signup")
    public AuthResponseDTO signup(@Valid @RequestBody SignupRequestDTO signupRequest) throws MessagingException, IOException {
        // Check if email is already in use
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return new AuthResponseDTO("Email is already in use", null,null);
        }

        Patient user = new Patient();
        user.setFullName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        //user.setPhoneNumber(signupRequest.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(signupRequest.getRole() != null ? signupRequest.getRole() : Role.PATIENT);
        user.setDob(user.getDob());
        user.setVerified(false);
        userRepository.save(user);
        String otp = String.valueOf((int)(Math.random() * 900000 + 100000));

        VerificationToken tokenEntity = new VerificationToken();
        tokenEntity.setToken(otp);
        tokenEntity.setUser(user);
        tokenEntity.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        verificationTokenRepository.save(tokenEntity);

        // Send email
        emailService.sendOtpEmailHtml(user.getEmail(),user.getFullName(), otp);
        return new AuthResponseDTO("User registered. Please verify OTP sent to your email.", null,null);
    }


    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody SigninRequestDTO signinRequest) {
        Map<String, String> responseBody = new HashMap<>();

        Optional<User> userOptional = userRepository.findByEmail(signinRequest.getEmail());
        if (userOptional.isEmpty()) {
            responseBody.put("message","User not found");
            return ResponseEntity.badRequest().body(responseBody);
        }
        User user = userOptional.get();
//        if (user.isAccountLocked()) {
//            responseBody.put("message","Account is locked. Please reset password or contact support.");
//            return ResponseEntity.badRequest().body(responseBody);
//        }
//        if (!user.isVerified()) {
//            responseBody.put("message","Please verify your email before signing in.");
//            return ResponseEntity.badRequest().body(responseBody);
//        }
//        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
//            user.setLoginAttempts(user.getLoginAttempts() + 1);
//            if (user.getLoginAttempts() >= 5) {
//                user.setAccountLocked(true);
//            }
//            userRepository.save(user);
//            responseBody.put("message","Invalid credentials");
//            return ResponseEntity.badRequest().body(responseBody);
//        }

        //user.setLoginAttempts(0);
        //userRepository.save(user);
        String token = jwtTokenUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponseDTO("Sign-in successful", token,user));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String otp) {
        Map<String, String> responseBody = new HashMap<>();

        Optional<VerificationToken> tokenEntity = verificationTokenRepository.findByToken(otp);
        if(tokenEntity.isEmpty()){
            responseBody.put("message", "Invalid OTP");
            return ResponseEntity.badRequest().body(responseBody);
        }

        if (tokenEntity.get().isUsed() || tokenEntity.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("OTP expired or already used");
        }

        User user = tokenEntity.get().getUser();
        user.setVerified(true);
        userRepository.save(user);

        tokenEntity.get().setUsed(true);
        verificationTokenRepository.save(tokenEntity.get());

        responseBody.put("message", "Account verified successfully.");
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws MessagingException, IOException {
        Map<String, String> responseBody = new HashMap<>();
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()) {
            responseBody.put("message", "User not found");
            return ResponseEntity.badRequest().body(responseBody);
        }

        String token = UUID.randomUUID().toString();

        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user.get());
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        resetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        emailService.sendResetPasswordEmailHtml(user.get().getEmail(),user.get().getFullName(), resetLink);

        responseBody.put("message", "Reset link has been sent to your email");
        return ResponseEntity.ok(responseBody);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        Map<String, String> responseBody = new HashMap<>();
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        Optional<ResetToken> optionalResetToken = resetTokenRepository.findByToken(token);
        if (optionalResetToken.isEmpty()) {
            responseBody.put("message", "Invalid token or token not found.");
            return ResponseEntity.badRequest().body(responseBody);
        }
        ResetToken resetToken = optionalResetToken.get();

        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            responseBody.put("message", "Token expired or already used.");
            return ResponseEntity.badRequest().body(responseBody);
        }
        User user = resetToken.getUser();
        if (user == null) {
            responseBody.put("message", "No user associated with this token.");
            return ResponseEntity.badRequest().body(responseBody);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
        responseBody.put("message", "Password reset successful.");
        return ResponseEntity.ok(responseBody);
    }


    @GetMapping("/google")
    public AuthResponseDTO googleLoginSuccess(@RequestParam("token") String googleToken) {
        // This endpoint is just a placeholder if you eventually do Google OAuth2
        return new AuthResponseDTO("Google login successful", googleToken,null);
    }
}
