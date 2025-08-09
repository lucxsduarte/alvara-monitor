package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.repositories.UserRepository;
import com.empresa.contabilidade.alvara_monitor.services.SpreadsheetService;
import com.empresa.contabilidade.alvara_monitor.services.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpreadsheetController.class)
@Import(SpreadsheetControllerTest.TestConfig.class)
class SpreadsheetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpreadsheetService spreadsheetService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public SpreadsheetService spreadsheetService() {
            return Mockito.mock(SpreadsheetService.class);
        }

        @Bean
        @Primary
        public TokenService tokenService() {
            return Mockito.mock(TokenService.class);
        }

        @Bean
        @Primary
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }
    }

    @Test
    @DisplayName("Deve retornar status 200 OK e mensagem de sucesso ao importar planilha")
    void shouldReturnOkWhenImportingSpreadsheet() throws Exception {
        doNothing().when(spreadsheetService).importSpreadsheet();

        mockMvc.perform(get("/api/spreadsheet/import-spreadsheet"))
                .andExpect(status().isOk())
                .andExpect(content().string("Importação de planilha iniciada!"));

        verify(spreadsheetService).importSpreadsheet();
    }
}