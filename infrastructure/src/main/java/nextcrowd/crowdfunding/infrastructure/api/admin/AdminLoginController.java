package nextcrowd.crowdfunding.infrastructure.api.admin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import nextcrowd.crowdfunding.infrastructure.security.persistence.Role;
import nextcrowd.crowdfunding.infrastructure.security.persistence.User;
import nextcrowd.crowdfunding.infrastructure.security.service.AuthenticationService;

@Controller
public class AdminLoginController {

    private final AuthenticationService authenticationService;

    public AdminLoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/admin/login")
    public ResponseEntity<TokenDTO> login(@RequestBody AuthRequest loginRequest) {
        return ResponseEntity.ok(new TokenDTO(authenticationService.authenticate(loginRequest.username(), loginRequest.password())));
    }

    @PostMapping("/admin/sign_in")
    public ResponseEntity<Void> signIn(@RequestBody SignInRequest signInRequest) {
        authenticationService.signIn(signInRequest.username(), signInRequest.password(), signInRequest.fullName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/user")
    public ResponseEntity<UserDTO> user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(UserDTO.builder()
                                        .id(user.getId().toString())
                                        .email(user.getEmail())
                                        .fullName(user.getFullName())
                                        .roles(Optional.ofNullable(user.getRoles())
                                                       .orElseGet(Collections::emptySet)
                                                       .stream()
                                                       .map(Role::getRole)
                                                       .collect(Collectors.toSet()))
                                        .build());
    }


}
