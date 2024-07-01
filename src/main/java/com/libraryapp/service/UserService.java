package com.libraryapp.service;

import com.libraryapp.dto.user.UserRegistrationRequestDto;
import com.libraryapp.dto.user.UserResponseDto;
import com.libraryapp.dto.user.UserRoleUpdateRequestDto;
import com.libraryapp.dto.user.UserUpdateRequestDto;
import com.libraryapp.exception.custom.RegistrationException;

public interface UserService {

    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto setRole(String id, UserRoleUpdateRequestDto request);

    UserResponseDto getProfile();

    void deleteById(String id);

    UserResponseDto updateProfile(UserUpdateRequestDto request);
}
