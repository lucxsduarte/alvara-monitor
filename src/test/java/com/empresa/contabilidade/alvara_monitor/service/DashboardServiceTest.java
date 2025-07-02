package com.empresa.contabilidade.alvara_monitor.service;

import com.empresa.contabilidade.alvara_monitor.dto.AlvaraVencendoDTO;
import com.empresa.contabilidade.alvara_monitor.repository.EmpresaRepository;
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
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("Deve retornar o sumário do dashboard com dados agregados corretamente")
    void retornarSummaryCorretamente() {
        final var hoje = LocalDate.now();
        final var daqui30Dias = hoje.plusDays(30);

        final var listaVencendo30 = new ArrayList<>(List.of(
                new AlvaraVencendoDTO(1L, "Empresa A", "Bombeiros", hoje.plusDays(10)),
                new AlvaraVencendoDTO(2L, "Empresa B", "Funcionamento", hoje.plusDays(20))
        ));
        final var listaProximos = new ArrayList<>(List.of(
                new AlvaraVencendoDTO(3L, "C", "", hoje.plusDays(40)),
                new AlvaraVencendoDTO(4L, "D", "", hoje.plusDays(50)),
                new AlvaraVencendoDTO(5L, "E", "", hoje.plusDays(60)),
                new AlvaraVencendoDTO(6L, "F", "", hoje.plusDays(70)),
                new AlvaraVencendoDTO(7L, "G", "", hoje.plusDays(80))
        ));

        when(empresaRepository.count()).thenReturn(150L);
        when(empresaRepository.countAlvarasVencidos(hoje)).thenReturn(25L);
        when(empresaRepository.findAlvarasVencendoNoPeriodo(eq(hoje), eq(daqui30Dias))).thenReturn(listaVencendo30);
        when(empresaRepository.findAlvarasVencendoNoPeriodo(eq(daqui30Dias.plusDays(1)), any(LocalDate.class))).thenReturn(listaProximos);

        final var summary = dashboardService.getSummary();

        assertNotNull(summary);
        assertEquals(150L, summary.totalEmpresas());
        assertEquals(25L, summary.totalAlvarasVencidos());
        assertEquals(2, summary.alvarasVencendo30Dias().size());
        assertEquals(3, summary.proximosVencimentos().size());
    }
}