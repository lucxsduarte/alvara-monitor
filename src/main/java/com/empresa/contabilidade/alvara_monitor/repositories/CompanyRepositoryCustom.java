package com.empresa.contabilidade.alvara_monitor.repositories;

import com.empresa.contabilidade.alvara_monitor.dtos.ExpiringLicenseDTO;
import com.empresa.contabilidade.alvara_monitor.entities.Company;

import java.time.LocalDate;
import java.util.List;

public interface CompanyRepositoryCustom {

    long countExpLicenses(LocalDate referenceDate);

    List<ExpiringLicenseDTO> findExpLicensesInterval(LocalDate startDate, LocalDate endDate);

    List<Company> findExpLicenses(LocalDate referenceDate);

    List<Company> findExpLicensesOnDates(List<LocalDate> dates);
}
