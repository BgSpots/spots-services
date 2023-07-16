package com.spots.controller;

import com.spots.domain.Spot;
import com.spots.domain.User;
import com.spots.service.auth.EmailTakenException;
import com.spots.service.auth.InvalidInputException;
import com.spots.service.spots.SpotsService;
import com.spots.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/users")
class UserRestController {
    @Autowired
    public UserService userService;

    @GetMapping
    @Operation(summary = "Show all users", description = "Returns a list of user entity.")
    public ResponseEntity<?> getUsers(HttpServletRequest request){
        try {
            List<User> users =userService.getUsers();
            return ResponseEntity.ok(users);
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @PostMapping("/user")
    @Operation(summary = "Adds user", description = "Adds a new user entity.")
    public ResponseEntity<?>  addUser(@RequestBody User user, HttpServletRequest request){
        try {
            user.setId(randomId());

            ApiSuccess successResponse=new ApiSuccess("addUser",userService.createUser(user));
            return ResponseEntity.ok(successResponse);
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/user")
    @Operation(summary = "Updates user information", description = "Updates user entity.")
    public ResponseEntity<?>  updateUser(@RequestBody User user,HttpServletRequest request){
        try {

            ApiSuccess successResponse=new ApiSuccess("updateSpot",userService.updateUser(user));
            return ResponseEntity.ok(successResponse);
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Deletes user", description = "Deletes specific user by id.")
    public ResponseEntity<?>  deleteUser(@PathVariable String userId,HttpServletRequest request){
        try {
            ApiSuccess successResponse=new ApiSuccess("deleteSpot",userService.deleteUser(userId));
            return ResponseEntity.ok(successResponse);
        } catch (EmailTakenException | InvalidInputException e) {
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
