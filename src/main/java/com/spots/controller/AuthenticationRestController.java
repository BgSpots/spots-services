package com.spots.controller;

import com.spots.common.auth.LoginBody;
import com.spots.common.auth.LoginResponse;
import com.spots.common.auth.RegisterBody;
import com.spots.service.auth.AuthenticationService;
import com.spots.service.auth.EmailTakenException;
import com.spots.service.auth.InvalidInputException;
import com.spots.service.auth.InvalidLoginCredenials;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {
    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterBody body, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(
                    authService.register(body.toBuilder().ip(request.getRemoteAddr()).build()));
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginBody body, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(
                    authService.login(body.toBuilder().ip(request.getRemoteAddr()).build()));
        } catch (InvalidLoginCredenials e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/login/google")
    public ResponseEntity<LoginResponse> loginWithGoogle(@RequestBody String accessToken) {
        // TODO
        authService.loginWithGoogle(accessToken);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/login/facebook")
    public ResponseEntity<LoginResponse> loginWithFacebook(@RequestBody String accessToken) {
        // TODO
        return ResponseEntity.ok(null);
    }

    @PostMapping("/login/instagram")
    public ResponseEntity<LoginResponse> loginWithInstagram(@RequestBody String accessToken) {
        // TODO
        return ResponseEntity.ok(null);
    }
}
