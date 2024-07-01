package com.libraryapp.controller;

import com.libraryapp.dto.user.UserResponseDto;
import com.libraryapp.dto.user.UserRoleUpdateRequestDto;
import com.libraryapp.dto.user.UserUpdateRequestDto;
import com.libraryapp.service.UserService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    public UserResponseDto setRole(@PathVariable String id,
                                   @RequestBody @Valid UserRoleUpdateRequestDto request) {
        return userService.setRole(id, request);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public UserResponseDto getProfile() {
        return userService.getProfile();
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public UserResponseDto updateProfile(@RequestBody @Valid UserUpdateRequestDto request) {
        return userService.updateProfile(request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        userService.deleteById(id);
        return ResponseEntity.ok("User (ID: " + id + ") successfully deleted.");
    }
}
