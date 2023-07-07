package com.spots.controller.auth;

import com.spots.common.auth.AuthenticationRequest;
import com.spots.common.auth.AuthenticationResponse;
import com.spots.common.auth.RegisterRequest;
import com.spots.service.auth.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
class AuthenticationRestController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(
                authService.register(request.toBuilder().ip(httpServletRequest.getRemoteAddr()).build()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(
                authService.login(request.toBuilder().ip(httpServletRequest.getRemoteAddr()).build()));
    }

    @PostMapping("/login/facebook")
    public ResponseEntity<AuthenticationResponse> loginWithFacebook(
            @RequestBody AuthenticationRequest request) {
        // TODO
        return ResponseEntity.ok(null);
    }

    @PostMapping("/login/instagram")
    public ResponseEntity<AuthenticationResponse> loginWithInstagram(
            @RequestBody AuthenticationRequest request) {
        // TODO
        return ResponseEntity.ok(null);
    }
}
