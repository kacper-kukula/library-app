package com.libraryapp.dto.user;

import com.libraryapp.model.User;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateRequestDto(
        @NotNull
        User.Role role
) {
}
