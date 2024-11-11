package nextcrowd.crowdfunding.infrastructure.security.service;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import nextcrowd.crowdfunding.infrastructure.security.persistence.User;
import nextcrowd.crowdfunding.infrastructure.security.persistence.UserRepository;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthenticationService(
            AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    public String authenticate(String username, String password) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return jwtService.generateToken(authenticate.getName());

    }

    public void signIn(String username, String password, String fullName) {
        if (userRepository.findByEmail(username).isPresent()) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        User entity = User.builder()
                          .id(UUID.randomUUID())
                          .roles(new HashSet<>())
                          .email(username)
                          .createdAt(new Date())
                          .fullName(fullName)
                          .password(passwordEncoder.encode(password))
                          .build();
        userRepository.save(entity);
    }

}
