package com.project.agriculture.service;

import com.project.agriculture.dto.AuthResponse;
import com.project.agriculture.dto.LoginRequest;
import com.project.agriculture.dto.RegisterRequest;
import com.project.agriculture.entity.Role;
import com.project.agriculture.entity.User;
import com.project.agriculture.exception.DuplicateResourceException;
import com.project.agriculture.repository.UserRepository;
import com.project.agriculture.security.JwtUtil;
import com.project.agriculture.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // NEVER store plain text
        user.setPhone(request.getPhone());

        // Public registration always creates a FARMER account.
        // To create an ADMIN account: register normally, then update the role
        // directly in the database — e.g.
        //   UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
        // This keeps admin creation out of reach of the public API, which is
        // the safer approach for a project like this.
        user.setRole(Role.FARMER);

        User saved = userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(saved);
        String token = jwtUtil.generateToken(principal);

        return new AuthResponse(token, saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        // This throws BadCredentialsException automatically if email/password don't match —
        // handled globally by GlobalExceptionHandler, returns a clean 401.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User authenticated but not found — this should not happen"));

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtUtil.generateToken(principal);

        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
