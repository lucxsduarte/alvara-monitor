package com.empresa.contabilidade.alvara_monitor.services;

import com.empresa.contabilidade.alvara_monitor.entities.Company;
import com.empresa.contabilidade.alvara_monitor.repositories.CompanyRepository;
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
class SpreadsheetServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CsvDataProvider csvDataProvider;

    @InjectMocks
    private SpreadsheetService spreadsheetService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(spreadsheetService, "spreadsheetUrl", "http://fake.url/planilha.csv");
    }

    @Test
    @DisplayName("Deve importar apenas empresas novas e ignorar as existentes")
    void shouldImportOnlyNewCompaniesAndIgnoreExistingOnes() throws IOException {
        final var csvContent = "Nome,Bombeiros,Vigilancia,Policia,Funcionamento\n" +
                "Empresa Nova,01/10/2025,02/11/2025,,\n" +
                "Empresa Existente,15/08/2026,,,\n" +
                "Outra Empresa Nova,20/05/2027,,,,";

        final var fakeInputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        when(csvDataProvider.readDataFromUrl(anyString())).thenReturn(fakeInputStream);

        when(companyRepository.existsByName("Empresa Nova")).thenReturn(false);
        when(companyRepository.existsByName("Empresa Existente")).thenReturn(true);
        when(companyRepository.existsByName("Outra Empresa Nova")).thenReturn(false);


        spreadsheetService.importSpreadsheet();


        verify(companyRepository, times(2)).save(any(Company.class));

        final var companyCaptor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository, times(2)).save(companyCaptor.capture());

        final var savedCompanies = companyCaptor.getAllValues();
        assertEquals("Empresa Nova", savedCompanies.getFirst().getName());
        assertEquals(LocalDate.of(2025, 10, 1), savedCompanies.getFirst().getExpLicenseFiredept());
        assertEquals(LocalDate.of(2025, 11, 2), savedCompanies.getFirst().getExpLicenseSurveillance());

        assertEquals("Outra Empresa Nova", savedCompanies.get(1).getName());
        assertEquals(LocalDate.of(2027, 5, 20), savedCompanies.get(1).getExpLicenseFiredept());
    }
}