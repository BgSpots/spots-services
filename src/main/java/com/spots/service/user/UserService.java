package com.spots.service.user;

import com.spots.common.GenericValidator;
import com.spots.common.input.UserBody;
import com.spots.domain.User;
import com.spots.repository.UserRepository;
import com.spots.service.auth.EmailTakenException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GenericValidator<User> userValidator;
    private final PasswordEncoder passwordEncoder;
    public static AtomicLong userId = new AtomicLong(1L);

    public void createUser(UserBody userBody) {
        User user =
                User.builder()
                        .id(userId.get())
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
        userId.incrementAndGet();
    }

    public void updateUser(UserBody userBody) {
        if (userRepository.existsUserByEmail(userBody.getEmail())) {
            throw new EmailTakenException("User with that email already exists");
        }

        User user =
                userRepository
                        .findById(userBody.getId())
                        .orElseThrow(() -> new InvalidUserException("User does not exist!"));
        user.setEmail(userBody.getEmail());
        user.setUsername(userBody.getUsername());
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

    public User getUser(String email) {
        return userRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new InvalidUserException("Invalid email for user!"));
    }
}
