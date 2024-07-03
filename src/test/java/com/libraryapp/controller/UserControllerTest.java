package com.libraryapp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryapp.dto.user.UserResponseDto;
import com.libraryapp.dto.user.UserRoleUpdateRequestDto;
import com.libraryapp.dto.user.UserUpdateRequestDto;
import com.libraryapp.exception.custom.EntityNotFoundException;
import com.libraryapp.model.User;
import com.libraryapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponseDto userResponseDto;
    private UserRoleUpdateRequestDto userRoleUpdateRequestDto;
    private UserUpdateRequestDto userUpdateRequestDto;

    @BeforeEach
    void setUp() {
        userResponseDto = new UserResponseDto(
                "12345", "testuser@library.com", "Test", "User", User.Role.CUSTOMER);

        userRoleUpdateRequestDto = new UserRoleUpdateRequestDto(User.Role.MANAGER);

        userUpdateRequestDto = new UserUpdateRequestDto("Test", "Updated");
    }

    @Test
    @DisplayName("Update user role successfully")
    @WithMockUser(roles = {"MANAGER"})
    void setRole_ValidData_ReturnsUpdatedUserResponseDto() throws Exception {
        // Given
        Mockito.when(userService.setRole(Mockito.anyString(),
                        Mockito.any(UserRoleUpdateRequestDto.class)))
                .thenReturn(userResponseDto);

        // When & Then
        mockMvc.perform(put("/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRoleUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.id()))
                .andExpect(jsonPath("$.email").value(userResponseDto.email()))
                .andExpect(jsonPath("$.firstName").value(userResponseDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(userResponseDto.lastName()))
                .andExpect(jsonPath("$.role").value(userResponseDto.role().toString()));
    }

    @Test
    @DisplayName("Get user profile successfully")
    @WithMockUser(roles = {"MANAGER"})
    void getProfile_ValidRequest_ReturnsUserResponseDto() throws Exception {
        // Given
        Mockito.when(userService.getProfile())
                .thenReturn(userResponseDto);

        // When & Then
        mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.id()))
                .andExpect(jsonPath("$.email").value(userResponseDto.email()))
                .andExpect(jsonPath("$.firstName").value(userResponseDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(userResponseDto.lastName()))
                .andExpect(jsonPath("$.role").value(userResponseDto.role().toString()));
    }

    @Test
    @DisplayName("Update user profile successfully")
    @WithMockUser(roles = {"MANAGER"})
    void updateProfile_ValidData_ReturnsUpdatedUserResponseDto() throws Exception {
        // Given
        Mockito.when(userService.updateProfile(Mockito.any(UserUpdateRequestDto.class)))
                .thenReturn(userResponseDto);

        // When & Then
        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.id()))
                .andExpect(jsonPath("$.email").value(userResponseDto.email()))
                .andExpect(jsonPath("$.firstName").value(userResponseDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(userResponseDto.lastName()))
                .andExpect(jsonPath("$.role").value(userResponseDto.role().toString()));
    }

    @Test
    @DisplayName("Delete user by ID successfully")
    @WithMockUser(roles = {"MANAGER"})
    void deleteById_ExistingUser_ReturnsOk() throws Exception {
        // Given
        Mockito.doNothing().when(userService).deleteById("1");

        // When & Then
        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User (ID: 1) successfully deleted."));
    }

    @Test
    @DisplayName("Update user role with invalid data")
    @WithMockUser(roles = {"MANAGER"})
    void setRole_InvalidData_ReturnsBadRequest() throws Exception {
        // Given
        UserRoleUpdateRequestDto invalidRoleUpdateRequest =
                new UserRoleUpdateRequestDto(null); // Invalid role

        // When & Then
        mockMvc.perform(put("/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoleUpdateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get user profile when user not found")
    @WithMockUser(roles = {"MANAGER"})
    void getProfile_UserNotFound_ReturnsNotFound() throws Exception {
        // Given
        Mockito.when(userService.getProfile())
                .thenThrow(new EntityNotFoundException("User profile not found"));

        // When & Then
        mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
