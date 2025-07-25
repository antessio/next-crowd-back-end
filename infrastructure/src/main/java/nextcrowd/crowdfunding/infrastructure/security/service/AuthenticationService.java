package nextcrowd.crowdfunding.infrastructure.security.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import nextcrowd.crowdfunding.infrastructure.security.persistence.Role;
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
                new UsernamePasswordAuthenticationToken(username, password));
        return jwtService.generateToken(authenticate.getName());

    }

    public void signIn(String username, String password, String fullName, boolean isVerified, Set<String> roles) {
        Optional<User> maybeUser = userRepository.findByEmail(username);
        if (maybeUser.isPresent()) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        UUID userId = UUID.randomUUID();
        User entity = User.builder()
                          .id(userId)
                          .email(username)
                          .createdAt(new Date())
                          .fullName(fullName)
                          .password(passwordEncoder.encode(password))
                          .isVerified(isVerified)
                          .build();
        roles.forEach(entity::addRole);
        userRepository.save(entity);
    }

}
