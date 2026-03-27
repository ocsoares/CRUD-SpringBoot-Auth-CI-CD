package com.ocsoares.crud_springboot_auth_ci_cd.services;

import com.ocsoares.crud_springboot_auth_ci_cd.dtos.request.LoginRequestDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.dtos.request.RegisterRequestDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.dtos.response.AuthResponseDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.dtos.response.UserResponseDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.entities.UserEntity;
import com.ocsoares.crud_springboot_auth_ci_cd.exceptions.user.UserAlreadyExistsException;
import com.ocsoares.crud_springboot_auth_ci_cd.exceptions.user.UserNotFoundException;
import com.ocsoares.crud_springboot_auth_ci_cd.mapper.UserMapper;
import com.ocsoares.crud_springboot_auth_ci_cd.repositories.UserRepository;
import com.ocsoares.crud_springboot_auth_ci_cd.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private UserEntity user;
    private RegisterRequestDTO registerDTO;
    private LoginRequestDTO loginDTO;

    @BeforeEach
    void setUp() {
        user = new UserEntity(UUID.randomUUID(), "Cauã Soares", "caua@email.com", "encodedPassword");

        registerDTO = new RegisterRequestDTO("Cauã Soares", "caua@email.com", "12345678");
        loginDTO = new LoginRequestDTO("caua@email.com", "12345678");
    }

    // ======================== REGISTER ========================

    @Test
    @DisplayName("Should register user successfully and return UserResponseDTO")
    void shouldRegisterUserSuccessfully() {
        UserResponseDTO expectedResponse = new UserResponseDTO(user.getName(), user.getEmail());

        when(userRepository.findByEmail(registerDTO.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDTO.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponseDTO result = authService.register(registerDTO);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(registerDTO.email());
        assertThat(result.name()).isEqualTo(registerDTO.name());

        verify(userRepository).findByEmail(registerDTO.email());
        verify(passwordEncoder).encode(registerDTO.password());
        verify(userRepository).save(any(UserEntity.class));
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email is already registered")
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.findByEmail(registerDTO.email())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.register(registerDTO)).isInstanceOf(UserAlreadyExistsException.class)
                                                                   .hasMessage(
                                                                           UserAlreadyExistsException.EXCEPTION_MESSAGE);

        verify(userRepository).findByEmail(registerDTO.email());
        verify(userRepository, never()).save(any());
    }

    // ======================== LOGIN ========================

    @Test
    @DisplayName("Should login successfully and return AuthResponseDTO with token")
    void shouldLoginSuccessfully() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponseDTO result = authService.login(loginDTO);

        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo("jwt-token");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(loginDTO.email());
        verify(jwtService).generateToken(user);
    }

    @Test
    @DisplayName("Should throw AuthenticationException when credentials are invalid")
    void shouldThrowWhenCredentialsAreInvalid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(
                new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginDTO)).isInstanceOf(BadCredentialsException.class);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found after authentication")
    void shouldThrowWhenUserNotFoundAfterAuthentication() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginDTO)).isInstanceOf(UserNotFoundException.class)
                                                             .hasMessage(UserNotFoundException.EXCEPTION_MESSAGE);

        verify(jwtService, never()).generateToken(any());
    }
}