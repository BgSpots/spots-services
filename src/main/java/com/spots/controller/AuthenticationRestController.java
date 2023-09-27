package com.spots.controller;

import com.spots.common.input.LoginBody;
import com.spots.common.input.RegisterBody;
import com.spots.common.output.ApiError;
import com.spots.common.output.ApiSuccess;
import com.spots.service.auth.AuthenticationService;
import com.spots.service.auth.EmailTakenException;
import com.spots.service.auth.InvalidAccessTokenException;
import com.spots.service.auth.InvalidInputException;
import com.spots.service.auth.InvalidLoginCredenials;
import com.spots.service.auth.UserAlreadyExistsException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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

    /**
      * Generates jwt token for the user using email and password, saves the use to the database
      *
      * @param body
      * @param request
      * @return
      */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterBody body, HttpServletRequest request)
            throws MessagingException {
        try {
            authService.register(body.toBuilder().ip(request.getRemoteAddr()).build());
            ApiSuccess successResponse = new ApiSuccess("register", "Registered successfully!");
            return ResponseEntity.ok(successResponse);
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
      * Generates new jwt token for already existing user
      *
      * @param body
      * @param request
      * @return
      */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginBody body, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(
                    authService.login(body.toBuilder().ip(request.getRemoteAddr()).build()));
        } catch (InvalidLoginCredenials e) {
            ApiError error =
                    new ApiError(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
      * Blacklists the user's jwt token and make it so the user should authenticate with newly created
      * jwt token
      *
      * @param request
      * @return
      */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        authService.logout(request);
        ApiSuccess successResponse = new ApiSuccess("logout", "Logged out successfully!");
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/login/google")
    public ResponseEntity<?> loginWithGoogle(
            @RequestBody String accessToken, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(authService.loginWithGoogle(accessToken));
        } catch (UserAlreadyExistsException | InvalidAccessTokenException e) {
            ApiError error =
                    new ApiError(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login/facebook")
    public ResponseEntity<?> loginWithFacebook(
            @RequestBody String accessToken, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(authService.loginWithFacebook(accessToken));
        } catch (UserAlreadyExistsException | InvalidAccessTokenException e) {
            ApiError error =
                    new ApiError(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUserEmail(@RequestBody String code, HttpServletRequest request) {
        authService.verifyEmail(code);
        ApiSuccess successResponse = new ApiSuccess("verify email", "Verified email successfully!");
        return ResponseEntity.ok(successResponse);
    }
}
