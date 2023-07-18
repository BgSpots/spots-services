package com.spots.service.user;

import com.spots.domain.GenericValidator;
import com.spots.domain.Spot;
import com.spots.domain.User;
import com.spots.dto.UserDto;
import com.spots.repository.UserRepository;
import com.spots.service.auth.EmailTakenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public void createUser(User user){
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new EmailTakenException("User with that email already exists");
        }
        GenericValidator<User> spotValidator = new GenericValidator<>();
        spotValidator.validate(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.insert(user);

    }

    public void updateUser(UserDto userDto){
        if (userRepository.existsUserByEmail(userDto.getEmail())) {
            throw new EmailTakenException("User with that email already exists");
        }
        if(userRepository.existsById(userDto.getId())){
            throw new InvalidIdException("User with this id doesn't exist");
        }
        User user = userRepository.findById(userDto.getId()).get();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

    }

    public void deleteUser(String userId){
        if(userRepository.existsById(userId)){
            throw new InvalidIdException("User with this id doesn't exist");
        }
        userRepository.deleteById(userId);


    }
    public List<User> getUsers(){
        return  userRepository.findAll();
    }

}
