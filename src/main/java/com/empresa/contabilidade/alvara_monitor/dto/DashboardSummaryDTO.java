package com.empresa.contabilidade.alvara_monitor.dto;

import java.util.List;

public record DashboardSummaryDTO(
        long totalEmpresas,
        long totalAlvarasVencidos,
        List<AlvaraVencendoDTO> alvarasVencendo30Dias,
        List<AlvaraVencendoDTO> proximosVencimentos
) {
}
