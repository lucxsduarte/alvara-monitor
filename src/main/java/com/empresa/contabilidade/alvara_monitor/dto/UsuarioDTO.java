package com.empresa.contabilidade.alvara_monitor.dto;

public record UsuarioDTO(
        Long id,
        String login,
        String role
) {}