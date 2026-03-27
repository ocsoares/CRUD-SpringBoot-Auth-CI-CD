package com.ocsoares.crud_springboot_auth_ci_cd.services;

import com.ocsoares.crud_springboot_auth_ci_cd.dtos.response.UserResponseDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.entities.UserEntity;
import com.ocsoares.crud_springboot_auth_ci_cd.exceptions.user.UserNotFoundException;
import com.ocsoares.crud_springboot_auth_ci_cd.mapper.UserMapper;
import com.ocsoares.crud_springboot_auth_ci_cd.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDTO findUserByEmail(String email) {
        UserEntity userFound = this.userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        return this.userMapper.toResponse(userFound);
    }

    public List<UserResponseDTO> findAllUsers() {
        List<UserEntity> usersFound = this.userRepository.findAll();

        return this.userMapper.toResponseList(usersFound);
    }
}
