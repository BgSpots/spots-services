package com.spots.controller;


import com.spots.domain.User;
import com.spots.dto.UserDto;
import com.spots.service.auth.EmailTakenException;
import com.spots.service.auth.InvalidInputException;

import com.spots.service.user.InvalidIdException;
import com.spots.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "Bearer Authentication")
class UserRestController {
    @Autowired
    public UserService userService;

    @GetMapping
    @Operation(summary = "Show all users", description = "Returns a list of user entity.")
    public ResponseEntity<?> getUsers(HttpServletRequest request){
        List<User> users =userService.getUsers();
        return ResponseEntity.ok(users);

    }
    @PostMapping
    @Operation(summary = "Adds user", description = "Adds a new user entity.")
    public ResponseEntity<?>  addUser(@RequestBody UserDto userDto, HttpServletRequest request){
        try {
            User user = User.builder().id(randomId())
                    .username(userDto.getUsername())
                    .email(userDto.getEmail())
                    .password(userDto.getPassword())
                    .build();
            userService.createUser(user);
            ApiSuccess successResponse=new ApiSuccess("addUser","User added successfully!");
            return ResponseEntity.ok(successResponse);
        } catch (InvalidIdException |EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }

    }

    @PutMapping
    @Operation(summary = "Updates user information", description = "Updates user entity.")
    public ResponseEntity<?>  updateUser(@RequestBody UserDto userDto,HttpServletRequest request){
        try {
            userService.updateUser(userDto);
            ApiSuccess successResponse=new ApiSuccess("updateSpot","User updated successfully!");
            return ResponseEntity.ok(successResponse);
        } catch (InvalidIdException |EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }

    }
    @DeleteMapping("/{userId}")
    @Operation(summary = "Deletes user", description = "Deletes specific user by id.")
    public ResponseEntity<?>  deleteUser(@PathVariable String userId,HttpServletRequest request){
        try {
            userService.deleteUser(userId);
            ApiSuccess successResponse=new ApiSuccess("deleteSpot","User deleted successfully!");
            return ResponseEntity.ok(successResponse);
        } catch (InvalidIdException | InvalidInputException e ) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }

    }
    private static String randomId() {
        Long max=1000000L;
        Long min =9999999L;
        Random random = new Random();
        return min + random.nextLong() % (max - min + 1)+"";
    }
}
