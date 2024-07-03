package com.libraryapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.libraryapp.dto.user.UserRegistrationRequestDto;
import com.libraryapp.dto.user.UserResponseDto;
import com.libraryapp.dto.user.UserRoleUpdateRequestDto;
import com.libraryapp.dto.user.UserUpdateRequestDto;
import com.libraryapp.exception.custom.RegistrationException;
import com.libraryapp.mapper.UserMapper;
import com.libraryapp.model.User;
import com.libraryapp.repository.UserRepository;
import com.libraryapp.security.util.AuthenticationUtil;
import com.libraryapp.service.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationUtil authenticationUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Verify that register() method works with valid user")
    void register_ValidUser_ReturnsCorrectUserResponseDto() throws RegistrationException {
        // Given
        UserRegistrationRequestDto requestDto = getDummyUserRegistrationRequestDto();
        User user = getDummyUser();
        user.setPassword("encodedPassword");
        User savedUser = getDummyUser();
        savedUser.setId("1");
        UserResponseDto userResponseDto = getDummyUserResponseDto();

        when(userRepository.findByEmail(requestDto.email())).thenReturn(Optional.empty());
        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.password())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(userResponseDto);

        // When
        UserResponseDto actual = userService.register(requestDto);

        // Then
        assertThat(actual).isEqualTo(userResponseDto);
        verify(userRepository, times(1)).findByEmail(requestDto.email());
        verify(userMapper, times(1)).toEntity(requestDto);
        verify(passwordEncoder, times(1)).encode(requestDto.password());
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(savedUser);
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Verify that register() method throws exception for existing user")
    void register_ExistingUser_ThrowsException() {
        // Given
        UserRegistrationRequestDto requestDto = getDummyUserRegistrationRequestDto();
        User existingUser = getDummyUser();
        existingUser.setId("1");

        when(userRepository.findByEmail(requestDto.email())).thenReturn(Optional.of(existingUser));

        // When
        assertThrows(RegistrationException.class, () -> userService.register(requestDto));

        // Then
        verify(userRepository, times(1)).findByEmail(requestDto.email());
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Verify that setRole() method works")
    void setRole_ValidId_ReturnsUpdatedUserResponseDto() {
        // Given
        String userId = "1";
        final UserRoleUpdateRequestDto requestDto = new UserRoleUpdateRequestDto(User.Role.MANAGER);
        User user = getDummyUser();
        user.setId(userId);
        user.setRole(User.Role.MANAGER);
        User updatedUser = getDummyUser();
        updatedUser.setId(userId);
        updatedUser.setRole(User.Role.MANAGER);
        UserResponseDto userResponseDto = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                User.Role.MANAGER
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(userResponseDto);

        // When
        UserResponseDto actual = userService.setRole(userId, requestDto);

        // Then
        assertThat(actual).isEqualTo(userResponseDto);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(updatedUser);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Verify that setRole() method throws exception for invalid user ID")
    void setRole_InvalidId_ThrowsException() {
        // Given
        String userId = "1";
        UserRoleUpdateRequestDto requestDto = new UserRoleUpdateRequestDto(User.Role.MANAGER);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        assertThrows(UsernameNotFoundException.class,
                () -> userService.setRole(userId, requestDto));

        // Then
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Verify that getProfile() method works")
    void getProfile_ReturnsUserResponseDto() {
        // Given
        User user = getDummyUser();
        user.setId("1");
        UserResponseDto userResponseDto = getDummyUserResponseDto();

        when(authenticationUtil.getCurrentUserFromDb()).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        // When
        UserResponseDto actual = userService.getProfile();

        // Then
        assertThat(actual).isEqualTo(userResponseDto);
        verify(authenticationUtil, times(1)).getCurrentUserFromDb();
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(authenticationUtil, userMapper);
    }

    @Test
    @DisplayName("Verify that getProfile() method throws exception for deleted user")
    void getProfile_DeletedUser_ThrowsException() {
        // Given
        User user = getDummyUser();
        user.setId("1");
        user.setIsDeleted(true);

        when(authenticationUtil.getCurrentUserFromDb()).thenReturn(user);

        // When
        assertThrows(UsernameNotFoundException.class, () -> userService.getProfile());

        // Then
        verify(authenticationUtil, times(1)).getCurrentUserFromDb();
        verifyNoMoreInteractions(authenticationUtil);
    }

    @Test
    @DisplayName("Verify that deleteById() method works")
    void deleteById_ValidId_MarksUserAsDeleted() {
        // Given
        String userId = "1";
        User user = getDummyUser();
        user.setId(userId);
        user.setIsDeleted(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.deleteById(userId);

        // Then
        assertThat(user.getIsDeleted()).isTrue();
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Verify that deleteById() method throws exception for invalid user ID")
    void deleteById_InvalidId_ThrowsException() {
        // Given
        String userId = "1";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        assertThrows(UsernameNotFoundException.class, () -> userService.deleteById(userId));

        // Then
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Verify that updateProfile() method works")
    void updateProfile_ValidUserUpdateRequestDto_ReturnsUpdatedUserResponseDto() {
        // Given
        final UserUpdateRequestDto requestDto = new UserUpdateRequestDto("John", "Doe");
        User user = getDummyUser();
        user.setId("1");
        User updatedUser = getDummyUser();
        updatedUser.setId("1");
        updatedUser.setFirstName("John");
        updatedUser.setLastName("Doe");
        UserResponseDto userResponseDto = new UserResponseDto(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getRole()
        );

        when(authenticationUtil.getCurrentUserFromDb()).thenReturn(user);
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(userResponseDto);

        // When
        UserResponseDto actual = userService.updateProfile(requestDto);

        // Then
        assertThat(actual).isEqualTo(userResponseDto);
        verify(authenticationUtil, times(1)).getCurrentUserFromDb();
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).updateUserFromDto(user, requestDto);
        verify(userMapper, times(1)).toDto(updatedUser);
        verifyNoMoreInteractions(authenticationUtil, userRepository, userMapper);
    }

    @Test
    @DisplayName("Verify that updateProfile() method throws exception for deleted user")
    void updateProfile_DeletedUser_ThrowsException() {
        // Given
        final UserUpdateRequestDto requestDto = new UserUpdateRequestDto("John", "Doe");
        User user = getDummyUser();
        user.setId("1");
        user.setIsDeleted(true);

        when(authenticationUtil.getCurrentUserFromDb()).thenReturn(user);

        // When
        assertThrows(UsernameNotFoundException.class, () -> userService.updateProfile(requestDto));

        // Then
        verify(authenticationUtil, times(1)).getCurrentUserFromDb();
        verifyNoMoreInteractions(authenticationUtil);
    }

    private User getDummyUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(User.Role.CUSTOMER);
        user.setIsDeleted(false);
        return user;
    }

    private UserRegistrationRequestDto getDummyUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto("test@example.com", "password", "password",
                "John", "Doe");
    }

    private UserResponseDto getDummyUserResponseDto() {
        return new UserResponseDto("1", "test@example.com", "John", "Doe", User.Role.CUSTOMER);
    }
}
