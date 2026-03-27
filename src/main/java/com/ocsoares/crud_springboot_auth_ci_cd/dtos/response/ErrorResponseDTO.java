package com.ocsoares.crud_springboot_auth_ci_cd.dtos.response;

import java.time.LocalDateTime;

public record ErrorResponseDTO(int status, String error, Object message, LocalDateTime timestamp) {
    public ErrorResponseDTO(int status, String error, Object message) {
        this(status, error, message, LocalDateTime.now());
    }
}