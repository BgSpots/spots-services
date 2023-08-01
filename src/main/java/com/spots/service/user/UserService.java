package com.spots.service.user;

import com.spots.common.GenericValidator;
import com.spots.domain.User;
import com.spots.dto.UserDto;
import com.spots.repository.UserRepository;
import com.spots.service.auth.EmailTakenException;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GenericValidator<User> userValidator;
    private final PasswordEncoder passwordEncoder;

    public void createUser(UserDto userDto) {
        User user =
                User.builder()
                        .id(randomId())
                        .username(userDto.getUsername())
                        .email(userDto.getEmail())
                        .password(userDto.getPassword())
                        .build();

        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new EmailTakenException("User with that email already exists");
        }
        userValidator.validate(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.insert(user);
    }

    public void updateUser(UserDto userDto) {
        if (userRepository.existsUserByEmail(userDto.getEmail())) {
            throw new EmailTakenException("User with that email already exists");
        }

        User user =
                userRepository
                        .findById(userDto.getId())
                        .orElseThrow(() -> new InvalidUserException("User does not exist!"));
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userValidator.validate(user);
        userRepository.save(user);
    }

    public void deleteUser(String userId) {
        if (userRepository.existsById(userId)) {
            throw new InvalidUserException("User with this id doesn't exist");
        }
        userRepository.deleteById(userId);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    private static String randomId() {
        long max = 1000000L;
        long min = 9999999L;
        Random random = new Random();
        return min + random.nextLong() % (max - min + 1) + "";
    }
}
