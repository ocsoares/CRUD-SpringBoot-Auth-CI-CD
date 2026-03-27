package com.ocsoares.crud_springboot_auth_ci_cd.services;

import com.ocsoares.crud_springboot_auth_ci_cd.dtos.request.UpdateUserRequestDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.dtos.response.UserResponseDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.entities.UserEntity;
import com.ocsoares.crud_springboot_auth_ci_cd.exceptions.user.UserAlreadyExistsException;
import com.ocsoares.crud_springboot_auth_ci_cd.exceptions.user.UserNotFoundException;
import com.ocsoares.crud_springboot_auth_ci_cd.mapper.UserMapper;
import com.ocsoares.crud_springboot_auth_ci_cd.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO findUserByEmail(String email) {
        UserEntity userFound = this.userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        return this.userMapper.toResponse(userFound);
    }

    public List<UserResponseDTO> findAllUsers() {
        List<UserEntity> usersFound = this.userRepository.findAll();

        return this.userMapper.toResponseList(usersFound);
    }

    public UserResponseDTO updateUserByEmail(String email, UpdateUserRequestDTO updateUserRequestDTO) {
        UserEntity userFound = this.userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        updateUserRequestDTO.getEmail().ifPresent(newEmail -> {
            this.userRepository.findByEmail(newEmail).ifPresent(newUser -> {
                // Lança esse Erro apenas quando o email enviado já existe no banco e pertence a outro usuário (não do próprio)
                if (!newUser.getId().equals(userFound.getId())) {
                    throw new UserAlreadyExistsException();
                }
            });
            userFound.setEmail(newEmail);
        });

        updateUserRequestDTO.getName().ifPresent(userFound::setName);
        updateUserRequestDTO.getPassword().ifPresent(pass -> userFound.setPassword(this.passwordEncoder.encode(pass)));
        
        UserEntity userUpdated = this.userRepository.save(userFound);

        return this.userMapper.toResponse(userUpdated);
    }
}
