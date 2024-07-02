package com.libraryapp.controller;

import com.libraryapp.dto.user.UserResponseDto;
import com.libraryapp.dto.user.UserRoleUpdateRequestDto;
import com.libraryapp.dto.user.UserUpdateRequestDto;
import com.libraryapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Management",
        description = "Endpoints for managing user profiles and roles.")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Update user role",
            description = "Allows a manager to update the role of a specific user by ID.")
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    public UserResponseDto setRole(@PathVariable String id,
                                   @RequestBody @Valid UserRoleUpdateRequestDto request) {
        return userService.setRole(id, request);
    }

    @Operation(summary = "Get user profile",
            description = "Fetches the profile information of the currently authenticated user.")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public UserResponseDto getProfile() {
        return userService.getProfile();
    }

    @Operation(summary = "Update user profile",
            description = "Updates the profile information of the currently authenticated user.")
    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public UserResponseDto updateProfile(@RequestBody @Valid UserUpdateRequestDto request) {
        return userService.updateProfile(request);
    }

    @Operation(summary = "Soft delete user",
            description = "Marks the user as deleted. "
                    + "This action can only be performed by a manager.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        userService.deleteById(id);
        return ResponseEntity.ok("User (ID: " + id + ") successfully deleted.");
    }
}
