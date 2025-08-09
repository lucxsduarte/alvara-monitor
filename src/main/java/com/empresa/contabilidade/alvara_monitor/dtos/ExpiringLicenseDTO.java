package com.empresa.contabilidade.alvara_monitor.dtos;

import java.time.LocalDate;

public record ExpiringLicenseDTO(
        Long companyId,
        String companyName,
        String licenseType,
        LocalDate expirationDate
) {
}
