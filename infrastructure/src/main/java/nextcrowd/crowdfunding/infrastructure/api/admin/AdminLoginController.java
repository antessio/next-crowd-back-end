package nextcrowd.crowdfunding.infrastructure.api.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import nextcrowd.crowdfunding.infrastructure.security.service.AuthenticationService;

@Controller
public class AdminLoginController {

    private final AuthenticationService authenticationService;

    public AdminLoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/admin/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(loginRequest.username(), loginRequest.password()));
    }

    @PostMapping("/admin/sign_in")
    public ResponseEntity<Void> signIn(@RequestBody SignInRequest signInRequest) {
        authenticationService.signIn(signInRequest.username(), signInRequest.password(), signInRequest.fullName());
        return ResponseEntity.ok().build();
    }


}
