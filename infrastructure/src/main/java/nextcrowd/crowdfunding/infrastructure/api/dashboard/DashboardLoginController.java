package nextcrowd.crowdfunding.infrastructure.api.dashboard;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import nextcrowd.crowdfunding.infrastructure.api.admin.AuthRequest;
import nextcrowd.crowdfunding.infrastructure.api.admin.SignInRequest;
import nextcrowd.crowdfunding.infrastructure.api.admin.TokenDTO;
import nextcrowd.crowdfunding.infrastructure.api.admin.UserDTO;
import nextcrowd.crowdfunding.infrastructure.security.persistence.Role;
import nextcrowd.crowdfunding.infrastructure.security.persistence.User;
import nextcrowd.crowdfunding.infrastructure.security.service.AuthenticationService;

@Controller
public class DashboardLoginController {

    private final AuthenticationService authenticationService;

    public DashboardLoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/dashboard/login")
    public ResponseEntity<TokenDTO> login(@RequestBody AuthRequest loginRequest) {
        return ResponseEntity.ok(new TokenDTO(authenticationService.authenticate(loginRequest.username(), loginRequest.password())));
    }

    @PostMapping("/dashboard/sign_in")
    public ResponseEntity<Void> signIn(@RequestBody SignInRequest signInRequest) {
        // TODO: currently we are not verifying the user
        boolean isVerified = true;

        Set<String> roles = new HashSet<>();
        if (signInRequest.isProjectOwner()) {
            roles.add(Role.PROJECT_OWNER);
        }
        if (signInRequest.isBaker()) {
            roles.add(Role.BAKER);
        }
        authenticationService.signIn(signInRequest.username(), signInRequest.password(), signInRequest.fullName(), isVerified, roles);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard/user")
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
