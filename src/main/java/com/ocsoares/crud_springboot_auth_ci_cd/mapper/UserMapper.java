package com.ocsoares.crud_springboot_auth_ci_cd.mapper;

import com.ocsoares.crud_springboot_auth_ci_cd.dtos.response.UserResponseDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public UserResponseDTO toResponse(UserEntity userEntity) {
        return new UserResponseDTO(userEntity.getName(), userEntity.getEmail());
    }

    public List<UserResponseDTO> toResponseList(List<UserEntity> userEntities) {
        return userEntities.stream().map(this::toResponse).toList();
    }
}
