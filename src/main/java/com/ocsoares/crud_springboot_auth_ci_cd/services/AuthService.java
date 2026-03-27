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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        UserEntity user = new UserEntity(null, dto.name(), dto.email(), passwordEncoder.encode(dto.password()));

        UserEntity userCreated = userRepository.save(user);

        return this.userMapper.toResponse(userCreated);
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));

        UserEntity user = userRepository.findByEmail(dto.email()).orElseThrow(UserNotFoundException::new);

        return new AuthResponseDTO(jwtService.generateToken(user));
    }
}