package com.ocsoares.crud_springboot_auth_ci_cd.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

@Data // Gera getters, setters, equals, hashCode e toString
@NoArgsConstructor // O Jackson precisa de um construtor vazio
@AllArgsConstructor // Opcional, se quiser criar o DTO manualmente em testes
public class UpdateUserRequestDTO {

    private JsonNullable<@NotBlank String> name = JsonNullable.undefined();

    private JsonNullable<@Email @NotBlank String> email = JsonNullable.undefined();

    private JsonNullable<@NotBlank String> password = JsonNullable.undefined();
}