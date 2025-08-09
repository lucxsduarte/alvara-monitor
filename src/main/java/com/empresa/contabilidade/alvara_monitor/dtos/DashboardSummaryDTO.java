package com.empresa.contabilidade.alvara_monitor.dtos;

import java.util.List;

public record DashboardSummaryDTO(
        long totalCompanies,
        long totalExpLicenses,
        List<ExpiringLicenseDTO> licensesExpiring30Days,
        List<ExpiringLicenseDTO> upcomingExpirations
) {
}
