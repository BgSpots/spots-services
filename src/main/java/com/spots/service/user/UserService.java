package com.spots.service.user;

import static com.spots.SpotsServicesApplication.IMAGE_DIR;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Random;
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

    private static String generateRandomName(int length) {
        // Define the characters that can be used in the random string
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        // Create a StringBuilder to store the random string
        StringBuilder randomString = new StringBuilder();

        // Create a Random object
        Random random = new Random();

        // Generate random characters and append them to the StringBuilder
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomString.append(randomChar);
        }

        // Convert StringBuilder to String and return
        return randomString.toString();
    }

    private String createImage(String base64) {
        byte[] imageBytes = Base64.getDecoder().decode(base64);
        String imageName = "image_" + generateRandomName(6) + ".png";

        String imagePath = IMAGE_DIR + imageName;

        try {
            Path imagePathObj = Path.of(imagePath);
            Files.write(imagePathObj, imageBytes, StandardOpenOption.CREATE);

            return imageName;
        } catch (Exception e) {
            throw new InvalidImageException(
                    "Something went wrong recreating the image " + e.getMessage());
        }
    }

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
        user.setImageName(createImage(userBody.getImageName()));
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
