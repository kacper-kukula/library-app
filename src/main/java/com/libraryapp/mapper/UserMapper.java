package com.libraryapp.mapper;

import com.libraryapp.config.MapperConfig;
import com.libraryapp.dto.user.UserRegistrationRequestDto;
import com.libraryapp.dto.user.UserResponseDto;
import com.libraryapp.dto.user.UserUpdateRequestDto;
import com.libraryapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    UserResponseDto toDto(User user);

    User toEntity(UserRegistrationRequestDto requestDto);

    void updateLoanFromDto(@MappingTarget User user, UserUpdateRequestDto dto);
}
