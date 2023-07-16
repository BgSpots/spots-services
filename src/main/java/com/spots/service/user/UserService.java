package com.spots.service.user;

import com.spots.domain.Spot;
import com.spots.domain.User;
import com.spots.dto.UserDto;
import com.spots.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public String createUser(User user){
        if(!userRepository.existsUserByEmail(user.getEmail())){
            userRepository.insert(user);
            return  "User added successfully!";
        }

        return "User with this email already exists!";
    }

    public String updateUser(UserDto userDto){

        if(!userRepository.existsById(userDto.getId())){
            User user = userRepository.findById(userDto.getId()).get();
            user.setEmail(userDto.getEmail());
            user.setUsername(userDto.getUsername());
            user.setPassword(userDto.getPassword());

            userRepository.save(user);
            return  "User updated successfully!";
        }

        return "User with this id doesn't exists!";

    }

    public String deleteUser(String userId){

        if(!userRepository.existsById(userId)){
            userRepository.deleteById(userId);
            return  "User deleted successfully!";
        }

        return "User with this id doesn't exists!";

    }
    public List<User> getUsers(){
        return  userRepository.findAll();
    }

}
