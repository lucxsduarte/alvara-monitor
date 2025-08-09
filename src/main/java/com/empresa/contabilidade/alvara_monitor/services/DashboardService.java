package com.empresa.contabilidade.alvara_monitor.services;

import com.empresa.contabilidade.alvara_monitor.dtos.ExpiringLicenseDTO;
import com.empresa.contabilidade.alvara_monitor.dtos.DashboardSummaryDTO;
import com.empresa.contabilidade.alvara_monitor.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CompanyRepository companyRepository;

    public DashboardSummaryDTO getSummary() {
        final var hoje = LocalDate.now();
        final var daqui30Dias = hoje.plusDays(30);

        final var totalEmpresas = companyRepository.count();
        final var totalAlvarasVencidos = companyRepository.countExpLicenses(hoje);
        final var alvarasVencendo30Dias = new ArrayList<>(companyRepository.findExpLicensesInterval(hoje, daqui30Dias));
        alvarasVencendo30Dias.sort(Comparator.comparing(ExpiringLicenseDTO::expirationDate));

        final var daqui90Dias = hoje.plusDays(90);

        final var proximos = new ArrayList<>(companyRepository.findExpLicensesInterval(daqui30Dias.plusDays(1), daqui90Dias));
        proximos.sort(Comparator.comparing(ExpiringLicenseDTO::expirationDate));

        final var proximosVencimentos = proximos.stream().limit(3).toList();

        return new DashboardSummaryDTO(
                totalEmpresas,
                totalAlvarasVencidos,
                alvarasVencendo30Dias,
                proximosVencimentos
        );
    }
}
