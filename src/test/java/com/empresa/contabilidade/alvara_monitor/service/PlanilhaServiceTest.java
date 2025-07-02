package com.empresa.contabilidade.alvara_monitor.service;

import com.empresa.contabilidade.alvara_monitor.model.Empresa;
import com.empresa.contabilidade.alvara_monitor.repository.EmpresaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanilhaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private CsvDataProvider csvDataProvider;

    @InjectMocks
    private PlanilhaService planilhaService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(planilhaService, "planilhaUrl", "http://fake.url/planilha.csv");
    }

    @Test
    @DisplayName("Deve importar apenas empresas novas e ignorar as existentes")
    void importarApenasEmpresasNovas() throws IOException {
        final var csvContent = "Nome,Bombeiros,Vigilancia,Policia,Funcionamento\n" +
                "Empresa Nova,01/10/2025,02/11/2025,,\n" +
                "Empresa Existente,15/08/2026,,,\n" +
                "Outra Empresa Nova,20/05/2027,,,,";

        final var inputStreamFake = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        when(csvDataProvider.lerDadosDaUrl(anyString())).thenReturn(inputStreamFake);

        when(empresaRepository.existsByNome("Empresa Nova")).thenReturn(false);
        when(empresaRepository.existsByNome("Empresa Existente")).thenReturn(true);
        when(empresaRepository.existsByNome("Outra Empresa Nova")).thenReturn(false);


        planilhaService.importarPlanilha();


        verify(empresaRepository, times(2)).save(any(Empresa.class));

        final var empresaCaptor = ArgumentCaptor.forClass(Empresa.class);
        verify(empresaRepository, times(2)).save(empresaCaptor.capture());

        final var empresasSalvas = empresaCaptor.getAllValues();
        assertEquals("Empresa Nova", empresasSalvas.getFirst().getNome());
        assertEquals(LocalDate.of(2025, 10, 1), empresasSalvas.getFirst().getVencBombeiros());
        assertEquals(LocalDate.of(2025, 11, 2), empresasSalvas.getFirst().getVencVigilancia());

        assertEquals("Outra Empresa Nova", empresasSalvas.get(1).getNome());
        assertEquals(LocalDate.of(2027, 5, 20), empresasSalvas.get(1).getVencBombeiros());
    }
}