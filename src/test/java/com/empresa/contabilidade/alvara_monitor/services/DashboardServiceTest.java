package com.empresa.contabilidade.alvara_monitor.services;

import com.empresa.contabilidade.alvara_monitor.dtos.ExpiringLicenseDTO;
import com.empresa.contabilidade.alvara_monitor.repositories.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("Deve retornar o sumário do dashboard com dados agregados corretamente")
    void shouldReturnDashboardSummaryCorrectly() {
        final var today = LocalDate.now();
        final var thirtyDaysFromNow = today.plusDays(30);

        final var expiringIn30DaysList = new ArrayList<>(List.of(
                new ExpiringLicenseDTO(1L, "Empresa A", "Bombeiros", today.plusDays(10)),
                new ExpiringLicenseDTO(2L, "Empresa B", "Funcionamento", today.plusDays(20))
        ));
        final var upcomingList = new ArrayList<>(List.of(
                new ExpiringLicenseDTO(3L, "C", "", today.plusDays(40)),
                new ExpiringLicenseDTO(4L, "D", "", today.plusDays(50)),
                new ExpiringLicenseDTO(5L, "E", "", today.plusDays(60)),
                new ExpiringLicenseDTO(6L, "F", "", today.plusDays(70)),
                new ExpiringLicenseDTO(7L, "G", "", today.plusDays(80))
        ));

        when(companyRepository.count()).thenReturn(150L);
        when(companyRepository.countExpLicenses(today)).thenReturn(25L);
        when(companyRepository.findExpLicensesInterval(eq(today), eq(thirtyDaysFromNow))).thenReturn(expiringIn30DaysList);
        when(companyRepository.findExpLicensesInterval(eq(thirtyDaysFromNow.plusDays(1)), any(LocalDate.class))).thenReturn(upcomingList);

        final var summary = dashboardService.getSummary();

        assertNotNull(summary);
        assertEquals(150L, summary.totalCompanies());
        assertEquals(25L, summary.totalExpLicenses());
        assertEquals(2, summary.licensesExpiring30Days().size());
        assertEquals(3, summary.upcomingExpirations().size());
    }
}