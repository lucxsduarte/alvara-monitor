package com.empresa.contabilidade.alvara_monitor.service;

import com.empresa.contabilidade.alvara_monitor.dto.AlvaraVencendoDTO;
import com.empresa.contabilidade.alvara_monitor.dto.DashboardSummaryDTO;
import com.empresa.contabilidade.alvara_monitor.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmpresaRepository empresaRepository;

    public DashboardSummaryDTO getSummary() {
        final var hoje = LocalDate.now();
        final var daqui30Dias = hoje.plusDays(30);

        final var totalEmpresas = empresaRepository.count();
        final var totalAlvarasVencidos = empresaRepository.countAlvarasVencidos(hoje);
        final var alvarasVencendo30Dias = new ArrayList<>(empresaRepository.findAlvarasVencendoNoPeriodo(hoje, daqui30Dias));
        alvarasVencendo30Dias.sort(Comparator.comparing(AlvaraVencendoDTO::dataVencimento));

        final var daqui90Dias = hoje.plusDays(90);

        final var proximos = new ArrayList<>(empresaRepository.findAlvarasVencendoNoPeriodo(daqui30Dias.plusDays(1), daqui90Dias));
        proximos.sort(Comparator.comparing(AlvaraVencendoDTO::dataVencimento));

        final var proximosVencimentos = proximos.stream().limit(3).toList();

        return new DashboardSummaryDTO(
                totalEmpresas,
                totalAlvarasVencidos,
                alvarasVencendo30Dias,
                proximosVencimentos
        );
    }
}
