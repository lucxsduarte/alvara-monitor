package com.empresa.contabilidade.alvara_monitor.dto;

import java.time.LocalDate;

public record AlvaraVencendoDTO(
        Long empresaId,
        String nomeEmpresa,
        String tipoAlvara,
        LocalDate dataVencimento
) {}
