package com.icklist.service;

import com.icklist.dto.LoginRequest;
import com.icklist.dto.LoginResponse;
import com.icklist.dto.SignupRequest;
import com.icklist.dto.SignupResponse;
import com.icklist.exception.EmailAlreadyExistsException;
import com.icklist.exception.InvalidCredentialsException;
import com.icklist.model.User;
import com.icklist.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final String[] AVATAR_PALETTE = {
            "#9E6752", // Terracotta
            "#2D4354", // Deep teal-blue
            "#534145", // Muted mauve
            "#73766A"  // Olive gray
    };

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    public SignupResponse signup(SignupRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        String normalizedEmail = request.getEmail().toLowerCase().trim();
        String trimmedName = request.getName().trim();

        // Check if user with this email already exists
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String userId = UUID.randomUUID().toString();
        String passwordHash = passwordEncoder.encode(request.getPassword());
        String createdAt = Instant.now().toString();

        String avatarInitials = deriveInitials(trimmedName);
        String avatarColor = deriveColor(trimmedName + userId);

        User newUser = new User(userId, normalizedEmail, passwordHash, trimmedName, avatarInitials, avatarColor, createdAt);
        userRepository.save(newUser);

        // Side effect: triggers welcome email via SES (graceful fallback/stubbed if SES unconfigured)
        emailService.sendWelcomeEmail(normalizedEmail);

        return new SignupResponse(userId, normalizedEmail, trimmedName, avatarInitials, avatarColor);
    }

    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);
        if (userOpt.isEmpty()) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getUserId(), user.getEmail());

        // Backwards compatibility for users created before name/avatar were added
        String name = user.getName() != null ? user.getName() : user.getEmail().split("@")[0];
        String initials = user.getAvatarInitials() != null ? user.getAvatarInitials() : deriveInitials(name);
        String color = user.getAvatarColor() != null ? user.getAvatarColor() : deriveColor(name);

        return new LoginResponse(token, user.getUserId(), name, initials, color);
    }

    public static String deriveInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "U";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            char first = parts[0].charAt(0);
            char last = parts[parts.length - 1].charAt(0);
            return ("" + first + last).toUpperCase();
        } else {
            String single = parts[0];
            return single.substring(0, Math.min(2, single.length())).toUpperCase();
        }
    }

    public static String deriveColor(String seed) {
        if (seed == null || seed.isEmpty()) {
            return AVATAR_PALETTE[0];
        }
        int hash = Math.abs(seed.hashCode());
        return AVATAR_PALETTE[hash % AVATAR_PALETTE.length];
    }
}
