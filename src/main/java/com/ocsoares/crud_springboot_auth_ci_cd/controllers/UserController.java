package com.ocsoares.crud_springboot_auth_ci_cd.controllers;

import com.ocsoares.crud_springboot_auth_ci_cd.dtos.response.UserResponseDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.services.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<UserResponseDTO> findUserByEmail(@RequestParam @NotBlank @Email String email) {
        return ResponseEntity.ok(this.userService.findUserByEmail(email));
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(this.userService.findAllUsers());
    }
}
