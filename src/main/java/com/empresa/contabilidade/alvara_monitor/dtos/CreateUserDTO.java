package com.empresa.contabilidade.alvara_monitor.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserDTO(
        @NotBlank(message = "O login não pode ser vazio.")
        @Size(min = 3, message = "O login deve ter no mínimo 3 caracteres.")
        String login,

        @NotBlank(message = "A senha não pode ser vazia.")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        String password,

        @NotBlank(message = "A role não pode ser vazia.")
        @Pattern(regexp = "ROLE_ADMIN|ROLE_USER", message = "A role deve ser ROLE_ADMIN ou ROLE_USER")
        String role
) {
}