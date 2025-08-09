package com.empresa.contabilidade.alvara_monitor.dtos;

public record UserResponseDTO(
        Long id,
        String login,
        String role
) {
}