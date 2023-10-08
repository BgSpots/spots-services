package com.spots.service.user;

import com.spots.common.GenericValidator;
import com.spots.common.input.ConquerBody;
import com.spots.common.input.UserBody;
import com.spots.common.output.UserDto;
import com.spots.domain.Spot;
import com.spots.domain.User;
import com.spots.repository.SpotsRepository;
import com.spots.repository.UserRepository;
import com.spots.service.auth.EmailTakenException;
import com.spots.service.common.SequenceGeneratorService;
import com.spots.service.spots.SpotConqueredException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SpotsRepository spotsRepository;
    private final GenericValidator<User> userValidator = new GenericValidator<>();
    private final PasswordEncoder passwordEncoder;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Transactional
    public void createUser(UserBody userBody) {
        User user =
                User.builder()
                        .id(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME))
                        .username(userBody.getUsername())
                        .email(userBody.getEmail())
                        .password(userBody.getPassword())
                        .build();
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new EmailTakenException("User with that email already exists");
        }
        userValidator.validate(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.insert(user);
    }

    @Transactional
    public void updateUser(UserBody userBody) {
        User user =
                userRepository
                        .findUserByEmail(userBody.getEmail())
                        .orElseThrow(() -> new InvalidUserException("User does not exist!"));
        user.setPicture(userBody.getPicture());
        userValidator.validate(user);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new InvalidUserException("User with this id doesn't exist");
        }
        userRepository.deleteById(userId);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public UserDto getUser(String email) {
        final var user =
                userRepository
                        .findUserByEmail(email)
                        .orElseThrow(() -> new InvalidUserException("Invalid email for user!"));

        final var timeUntilNextRoll =
                user.getNextRandomSpotGeneratedTime() == null
                        ? Duration.ZERO
                        : Duration.between(LocalDateTime.now(), user.getNextRandomSpotGeneratedTime());
        final var userDto = UserDto.fromUser(user);
        userDto.setTimeUntilNextRoll(timeUntilNextRoll.getSeconds());
        return userDto;
    }

    @Transactional
    public void conquerSpot(String email, ConquerBody conquerBody) {
        final var user =
                userRepository
                        .findUserByEmail(email)
                        .orElseThrow(() -> new SpotConqueredException("User doesn't exist"));
        if (user.getConqueredSpots().contains(conquerBody.getSpotId()))
            throw new SpotConqueredException("Spot is already conquered");
        user.getConqueredSpots().add(conquerBody.getSpotId());
        userRepository.save(user);
    }

    public List<Spot> getConqueredSpots(String email) {
        final var user =
                userRepository
                        .findUserByEmail(email)
                        .orElseThrow(() -> new SpotConqueredException("User doesn't exist"));
        return spotsRepository.findAllById(user.getConqueredSpots());
    }
}
