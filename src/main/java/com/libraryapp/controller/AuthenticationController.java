package com.libraryapp.controller;

import com.libraryapp.dto.user.UserLoginRequestDto;
import com.libraryapp.dto.user.UserLoginResponseDto;
import com.libraryapp.dto.user.UserRegistrationRequestDto;
import com.libraryapp.dto.user.UserResponseDto;
import com.libraryapp.exception.custom.RegistrationException;
import com.libraryapp.security.AuthenticationService;
import com.libraryapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Authentication",
        description = "APIs for user registration and login.")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new user",
            description = "Register a new user and return their details.")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @Operation(summary = "User login",
            description = "Authenticate user and return a JWT.")
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
