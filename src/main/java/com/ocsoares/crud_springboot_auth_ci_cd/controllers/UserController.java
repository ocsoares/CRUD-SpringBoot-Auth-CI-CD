package com.ocsoares.crud_springboot_auth_ci_cd.controllers;

import com.ocsoares.crud_springboot_auth_ci_cd.dtos.request.UpdateUserRequestDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.dtos.response.UserResponseDTO;
import com.ocsoares.crud_springboot_auth_ci_cd.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/search")
    public ResponseEntity<UserResponseDTO> updateUserByEmail(
            @RequestParam @NotBlank @Email String email,
            @RequestBody @Valid UpdateUserRequestDTO updateUserRequestDTO
    ) {
        return ResponseEntity.ok(this.userService.updateUserByEmail(email, updateUserRequestDTO));
    }

    @DeleteMapping("/search")
    public ResponseEntity<Void> deleteUserByEmail(@RequestParam @NotBlank @Email String email) {
        this.userService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }
}
