package com.empresa.contabilidade.alvara_monitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EditarUsuarioDTO(
        @NotBlank(message = "O login não pode ser vazio.")
        @Size(min = 3, message = "O login deve ter no mínimo 3 caracteres.")
        String login,

        @NotBlank(message = "A role não pode ser vazia.")
        @Pattern(regexp = "ROLE_ADMIN|ROLE_USER", message = "A role deve ser ROLE_ADMIN ou ROLE_USER")
        String role
) {
}