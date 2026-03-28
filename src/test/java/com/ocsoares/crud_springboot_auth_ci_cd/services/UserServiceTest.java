package com.ocsoares.crud_springboot_auth_ci_cd.services;

import com.ocsoares.crud_springboot_auth_ci_cd.dtos.request.UpdateUserRequestDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.dtos.response.UserResponseDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.entities.UserEntity;
import com.ocsoares.crud_springboot_auth_ci_cd.exceptions.user.UserAlreadyExistsException;
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
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    private PasswordEncoder passwordEncoder;

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

    // ======================== UPDATE BY EMAIL ========================

    @Test
    @DisplayName("Should update user name successfully")
    void shouldUpdateUserNameSuccessfully() {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO(JsonNullable.of("Novo Nome"), JsonNullable.undefined(),
                JsonNullable.undefined()
        );

        UserResponseDTO expectedResponse = new UserResponseDTO("Novo Nome", user.getEmail());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.updateUserByEmail(user.getEmail(), dto);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Novo Nome");

        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Should update user email successfully")
    void shouldUpdateUserEmailSuccessfully() {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO(JsonNullable.undefined(), JsonNullable.of("novo@email.com"),
                JsonNullable.undefined()
        );

        UserResponseDTO expectedResponse = new UserResponseDTO(user.getName(), "novo@email.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.updateUserByEmail(user.getEmail(), dto);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("novo@email.com");

        verify(userRepository).findByEmail("novo@email.com");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should update user password successfully")
    void shouldUpdateUserPasswordSuccessfully() {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO(JsonNullable.undefined(), JsonNullable.undefined(),
                JsonNullable.of("novaSenha123")
        );

        UserResponseDTO expectedResponse = new UserResponseDTO(user.getName(), user.getEmail());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("encodedNovaSenha");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.updateUserByEmail(user.getEmail(), dto);

        assertThat(result).isNotNull();
        verify(passwordEncoder).encode("novaSenha123");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user to update is not found")
    void shouldThrowWhenUserToUpdateNotFound() {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO(JsonNullable.of("Novo Nome"), JsonNullable.undefined(),
                JsonNullable.undefined()
        );

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserByEmail(user.getEmail(), dto)).isInstanceOf(
                UserNotFoundException.class).hasMessage(UserNotFoundException.EXCEPTION_MESSAGE);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when new email belongs to another user")
    void shouldThrowWhenNewEmailBelongsToAnotherUser() {
        UserEntity anotherUser = new UserEntity(UUID.randomUUID(), "Outro Usuário", "outro@email.com",
                "encodedPassword"
        );

        UpdateUserRequestDTO dto = new UpdateUserRequestDTO(JsonNullable.undefined(),
                JsonNullable.of("outro@email.com"), JsonNullable.undefined()
        );

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("outro@email.com")).thenReturn(Optional.of(anotherUser));

        assertThatThrownBy(() -> userService.updateUserByEmail(user.getEmail(), dto)).isInstanceOf(
                UserAlreadyExistsException.class).hasMessage(UserAlreadyExistsException.EXCEPTION_MESSAGE);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not throw when new email belongs to the same user")
    void shouldNotThrowWhenNewEmailBelongsToSameUser() {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO(JsonNullable.undefined(), JsonNullable.of(user.getEmail()),
                JsonNullable.undefined()
        );

        UserResponseDTO expectedResponse = new UserResponseDTO(user.getName(), user.getEmail());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.updateUserByEmail(user.getEmail(), dto);

        assertThat(result).isNotNull();
        verify(userRepository).save(user);
    }

    // ======================== DELETE BY EMAIL ========================

    @Test
    @DisplayName("Should delete user successfully when user exists")
    void shouldDeleteUserSuccessfully() {

        userService.deleteUserByEmail(user.getEmail());

        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user to delete is not found")
    void shouldThrowWhenUserToDeleteNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserByEmail(user.getEmail())).isInstanceOf(
                UserNotFoundException.class).hasMessage(UserNotFoundException.EXCEPTION_MESSAGE);

        verify(userRepository, never()).delete(any());
    }
}