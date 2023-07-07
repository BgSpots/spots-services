package com.spots.service.auth;

import com.spots.common.auth.AuthenticationRequest;
import com.spots.common.auth.AuthenticationResponse;
import com.spots.common.auth.RegisterRequest;
import com.spots.domain.Role;
import com.spots.domain.User;
import com.spots.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        // build user
        var user =
                User.builder()
                        .username(request.getEmail())
                        .email(request.getEmail())
                        .role(Role.USER)
                        .password(passwordEncoder.encode(request.getPassword()))
                        .build();
        // throw if exists
        if (userRepository.existsUserByEmail(user.getEmail()))
            throw new EmailTakenException("User with that email already exists");
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        // return jwt token to client
        return AuthenticationResponse.builder().accessToken(jwtToken).build();
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        // get user
        var user =
                userRepository
                        .findUserByEmail(request.getEmail())
                        .orElseThrow(
                                () -> new UserDoesNotExistException("User with that email does not exist"));
        var jwtToken = jwtService.generateToken(user);
        // return jwt token to client
        return AuthenticationResponse.builder().accessToken(jwtToken).build();
    }
}
