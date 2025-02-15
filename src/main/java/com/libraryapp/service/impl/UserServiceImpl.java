package com.libraryapp.service.impl;

import com.libraryapp.dto.user.UserRegistrationRequestDto;
import com.libraryapp.dto.user.UserResponseDto;
import com.libraryapp.dto.user.UserRoleUpdateRequestDto;
import com.libraryapp.dto.user.UserUpdateRequestDto;
import com.libraryapp.exception.custom.RegistrationException;
import com.libraryapp.mapper.UserMapper;
import com.libraryapp.model.User;
import com.libraryapp.repository.UserRepository;
import com.libraryapp.security.util.AuthenticationUtil;
import com.libraryapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found.";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new RegistrationException("Can't register this user.");
        }

        User user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto setRole(String id, UserRoleUpdateRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
        user.setRole(request.role());
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserResponseDto getProfile() {
        User user = authenticationUtil.getCurrentUserFromDb();
        if (user.getIsDeleted()) {
            throw new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE);
        }

        return userMapper.toDto(user);
    }

    @Override
    public void deleteById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
        user.setIsDeleted(true); // Soft delete

        userRepository.save(user);
    }

    @Override
    public UserResponseDto updateProfile(UserUpdateRequestDto request) {
        User user = authenticationUtil.getCurrentUserFromDb();
        if (user.getIsDeleted()) {
            throw new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE);
        }
        userMapper.updateUserFromDto(user, request);
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }
}
