package com.libraryapp.dto.user;

import com.libraryapp.model.User;

public record UserResponseDto(
        String id,
        String email,
        String firstName,
        String lastName,
        User.Role role
) {
}
