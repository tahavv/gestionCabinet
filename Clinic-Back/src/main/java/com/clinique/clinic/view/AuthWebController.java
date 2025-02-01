package com.clinique.clinic.view;

import com.clinique.clinic.model.*;
import com.clinique.clinic.model.dto.*;
import com.clinique.clinic.model.enums.Role;
import com.clinique.clinic.repositories.*;
import com.clinique.clinic.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/web/auth")
public class AuthWebController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        return "redirect:/web/auth/login?logout";
    }


    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signupForm", new SignupRequestDTO());
        return "user/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("signupForm") @Valid SignupRequestDTO signupRequest,
                         BindingResult result, Model model) throws MessagingException, IOException {
        if (result.hasErrors()) {
            return "user/signup";
        }

        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Email is already in use");
            return "user/signup";
        }

        Patient user = new Patient();
        user.setFullName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        //user.setRole(Optional.ofNullable(signupRequest.getRole()).orElse(Role.PATIENT));
        user.setRole(Role.PATIENT);
        user.setVerified(false);

        userRepository.save(user);

        String otp = String.valueOf((int) (Math.random() * 900000 + 100000));
        VerificationToken tokenEntity = new VerificationToken();
        tokenEntity.setToken(otp);
        tokenEntity.setUser(user);
        tokenEntity.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        verificationTokenRepository.save(tokenEntity);

        emailService.sendOtpEmailHtml(user.getEmail(), user.getFullName(), otp);

        model.addAttribute("infoMessage", "Registration successful. Check your email for OTP.");
        return "redirect:/web/auth/verify-otp";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "registered", required = false) boolean registered, Model model) {
        model.addAttribute("signinForm", new SigninRequestDTO());
        if (registered) {
            model.addAttribute("infoMessage", "Registration successful. Please login.");
        }
        return "user/login";
    }

    @PostMapping("/login")
    public String signin(@ModelAttribute("signinForm") @Valid SigninRequestDTO signinRequest,
                         BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            return "user/login";
        }

        Optional<User> userOptional = userRepository.findByEmail(signinRequest.getEmail());
        if (userOptional.isEmpty() ||
                !passwordEncoder.matches(signinRequest.getPassword(), userOptional.get().getPassword())) {
            model.addAttribute("errorMessage", "Invalid email or password.");
            return "user/login";
        }

        User user = userOptional.get();
        if (!user.isVerified()) {
            model.addAttribute("errorMessage", "Please verify your email before logging in.");
            return "user/login";
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        HttpSession session = request.getSession(true);
        session.setAttribute("loggedUser", user);


        if (user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.DOCTOR) || user.getRole().equals(Role.STAFF)) {
            return "redirect:/web/admin/dashboard";
        }
        return "redirect:/web/welcome";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .orElse("");

            if (role.equals("ROLE_ADMIN") || role.equals("ROLE_DOCTOR") || role.equals("ROLE_STAFF")) {
                return "redirect:/web/admin/dashboard";
            } else if (role.equals("ROLE_PATIENT")) {
                return "redirect:/web/welcome";
            }
        }
        return "redirect:/web/auth/login";
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(Model model) {
        model.addAttribute("otp", "");
        return "user/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String otp, Model model) {
        Optional<VerificationToken> tokenEntity = verificationTokenRepository.findByToken(otp);
        if (tokenEntity.isEmpty() || tokenEntity.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("errorMessage", "Invalid or expired OTP.");
            return "user/verify-otp";
        }

        VerificationToken vt = tokenEntity.get();
        User user = vt.getUser();
        user.setVerified(true);
        userRepository.save(user);

        vt.setUsed(true);
        verificationTokenRepository.save(vt);

        model.addAttribute("infoMessage", "Account verified successfully. Please log in.");
        return "redirect:/web/auth/login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("email", "");
        return "user/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam("email") String email,
            Model model
    ) throws MessagingException, IOException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "User not found with email: " + email);
            return "user/forgot-password";
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();

        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        resetTokenRepository.save(resetToken);
        String resetLink = "http://localhost:8083/web/auth/reset-password?token=" + token;
        emailService.sendResetPasswordEmailHtml(user.getEmail(), user.getFullName(), resetLink);

        model.addAttribute("infoMessage", "A reset link has been sent to your email.");
        return "user/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(
            @RequestParam("token") String token,
            Model model
    ) {
        Optional<ResetToken> optionalResetToken = resetTokenRepository.findByToken(token);
        if (optionalResetToken.isEmpty()) {
            model.addAttribute("errorMessage", "Invalid or expired reset token.");
            return "user/reset-password";
        }

        ResetToken resetToken = optionalResetToken.get();
        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("errorMessage", "Token expired or already used.");
            return "user/reset-password";
        }
        //model.addAttribute("token", token);
        ResetPasswordRequest resetForm = new ResetPasswordRequest();
        resetForm.setToken(token);
        model.addAttribute("resetForm", resetForm);
        return "user/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            Model model
    ) {
        Optional<ResetToken> optionalResetToken = resetTokenRepository.findByToken(token);
        if (optionalResetToken.isEmpty()) {
            model.addAttribute("errorMessage", "Invalid or expired token.");
            return "user/reset-password";
        }
        ResetToken resetToken = optionalResetToken.get();
        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("errorMessage", "Token expired or already used.");
            return "user/reset-password";
        }
        User user = resetToken.getUser();
        if (user == null) {
            model.addAttribute("errorMessage", "No user associated with this token.");
            return "user/reset-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);

        model.addAttribute("infoMessage", "Password reset successful. You can now log in.");
        return "redirect:/web/auth/login";
    }

}
