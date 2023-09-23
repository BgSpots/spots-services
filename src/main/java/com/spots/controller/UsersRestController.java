package com.spots.controller;

import com.spots.common.input.ConquerBody;
import com.spots.common.input.UserBody;
import com.spots.common.output.ApiError;
import com.spots.common.output.ApiSuccess;
import com.spots.common.output.UserDto;
import com.spots.domain.User;
import com.spots.service.auth.EmailTakenException;
import com.spots.service.auth.InvalidInputException;
import com.spots.service.spots.SpotConqueredException;
import com.spots.service.user.InvalidUserException;
import com.spots.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
class UserRestController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Show all users", description = "Returns a list of user entity.")
    public ResponseEntity<?> getUsers(HttpServletRequest request) {
        List<User> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    @Operation(summary = "Get user", description = "Returns a list of user entity.")
    public ResponseEntity<?> getUser(@PathVariable String email, HttpServletRequest request) {
        UserDto user = userService.getUser(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(summary = "Adds user", description = "Adds a new user entity.")
    public ResponseEntity<?> addUser(@RequestBody UserBody userBody, HttpServletRequest request) {
        try {
            userService.createUser(userBody);
            ApiSuccess successResponse = new ApiSuccess("addUser", "User added successfully!");
            return ResponseEntity.ok(successResponse);
        } catch (InvalidUserException | EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping
    @Operation(summary = "Updates user information", description = "Updates user entity.")
    public ResponseEntity<?> updateUser(@RequestBody UserBody userBody, HttpServletRequest request) {
        try {
            userService.updateUser(userBody);
            ApiSuccess successResponse = new ApiSuccess("updateUser", "User updated successfully!");
            return ResponseEntity.ok(successResponse);
        } catch (InvalidUserException | EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Deletes user", description = "Deletes specific user by id.")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            userService.deleteUser(userId);
            ApiSuccess successResponse = new ApiSuccess("deleteUser", "User deleted successfully!");
            return ResponseEntity.ok(successResponse);
        } catch (InvalidUserException | InvalidInputException e) {
            ApiError error =
                    new ApiError(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{email}/conquered")
    @Operation(
            summary = "Gets spots that the user has visited",
            description = "Gets conquered spots.")
    public ResponseEntity<?> getConqueredSpots(
            @PathVariable String email, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(userService.getConqueredSpots(email));
        } catch (SpotConqueredException | InvalidInputException e) {
            ApiError error =
                    new ApiError(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{email}/conquered")
    @Operation(
            summary = " Adds user who have visited this spot",
            description = "Adds user entity to the spots conquered list.")
    public ResponseEntity<?> conquerSpot(
            @PathVariable String email,
            @RequestBody ConquerBody conquerBody,
            HttpServletRequest request) {
        try {
            userService.conquerSpot(email, conquerBody);
            ApiSuccess successResponse = new ApiSuccess("conquerSpot", "Spot conquered!");
            return ResponseEntity.ok(successResponse);
        } catch (SpotConqueredException | InvalidInputException e) {
            ApiError error =
                    new ApiError(
                            LocalDateTime.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
