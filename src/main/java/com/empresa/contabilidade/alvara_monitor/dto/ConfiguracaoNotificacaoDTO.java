package com.empresa.contabilidade.alvara_monitor.dto;

import java.util.List;

public record ConfiguracaoNotificacaoDTO(
        List<Integer> diasAlerta,
        List<String> emailsDestino
) {
}
