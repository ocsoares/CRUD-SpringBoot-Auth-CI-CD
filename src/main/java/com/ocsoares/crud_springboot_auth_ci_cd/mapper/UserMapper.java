package com.ocsoares.crud_springboot_auth_ci_cd.mapper;

import com.ocsoares.crud_springboot_auth_ci_cd.dtos.UserResponseDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDTO toResponse(UserEntity userEntity) {
        return new UserResponseDTO(userEntity.getName(), userEntity.getEmail());
    }
}
