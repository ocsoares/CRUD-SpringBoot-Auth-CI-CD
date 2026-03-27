package com.ocsoares.crud_springboot_auth_ci_cd.services;

import com.ocsoares.crud_springboot_auth_ci_cd.dtos.response.UserResponseDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.entities.UserEntity;
import com.ocsoares.crud_springboot_auth_ci_cd.exceptions.user.UserNotFoundException;
import com.ocsoares.crud_springboot_auth_ci_cd.mapper.UserMapper;
import com.ocsoares.crud_springboot_auth_ci_cd.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserEntity user;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        user = new UserEntity(UUID.randomUUID(), "Cauã Soares", "caua@email.com", "encodedPassword");

        userResponseDTO = new UserResponseDTO(user.getName(), user.getEmail());
    }

    // ======================== FIND BY EMAIL ========================

    @Test
    @DisplayName("Should return UserResponseDTO when user is found by email")
    void shouldReturnUserWhenFoundByEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.findUserByEmail(user.getEmail());

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(user.getEmail());
        assertThat(result.name()).isEqualTo(user.getName());

        verify(userRepository).findByEmail(user.getEmail());
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found by email")
    void shouldThrowWhenUserNotFoundByEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserByEmail(user.getEmail())).isInstanceOf(UserNotFoundException.class)
                                                                              .hasMessage(
                                                                                      UserNotFoundException.EXCEPTION_MESSAGE);

        verify(userRepository).findByEmail(user.getEmail());
        verify(userMapper, never()).toResponse(any());
    }

    // ======================== FIND ALL ========================

    @Test
    @DisplayName("Should return list of UserResponseDTO when users exist")
    void shouldReturnAllUsers() {
        UserEntity user2 = new UserEntity(UUID.randomUUID(), "João Silva", "joao@email.com", "encodedPassword");

        UserResponseDTO user2ResponseDTO = new UserResponseDTO(user2.getName(), user2.getEmail());

        List<UserEntity> users = List.of(user, user2);
        List<UserResponseDTO> expectedResponse = List.of(userResponseDTO, user2ResponseDTO);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponseList(users)).thenReturn(expectedResponse);

        List<UserResponseDTO> result = userService.findAllUsers();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).email()).isEqualTo(user.getEmail());
        assertThat(result.get(1).email()).isEqualTo(user2.getEmail());

        verify(userRepository).findAll();
        verify(userMapper).toResponseList(users);
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void shouldReturnEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());
        when(userMapper.toResponseList(List.of())).thenReturn(List.of());

        List<UserResponseDTO> result = userService.findAllUsers();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(userRepository).findAll();
        verify(userMapper).toResponseList(List.of());
    }
}