package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.repository.UsuarioRepository;
import com.empresa.contabilidade.alvara_monitor.service.PlanilhaService;
import com.empresa.contabilidade.alvara_monitor.service.TokenService;
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

@WebMvcTest(PlanilhaController.class)
@Import(PlanilhaControllerTest.TestConfig.class)
class PlanilhaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanilhaService planilhaService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public PlanilhaService planilhaService() {
            return Mockito.mock(PlanilhaService.class);
        }

        @Bean
        @Primary
        public TokenService tokenService() {
            return Mockito.mock(TokenService.class);
        }

        @Bean
        @Primary
        public UsuarioRepository usuarioRepository() {
            return Mockito.mock(UsuarioRepository.class);
        }
    }

    @Test
    @DisplayName("Deve retornar status 200 OK e mensagem de sucesso ao importar planilha")
    void deveRetornarOkAoImportarPlanilha() throws Exception {
        doNothing().when(planilhaService).importarPlanilha();

        mockMvc.perform(get("/api/planilha/importar-planilha"))
                .andExpect(status().isOk())
                .andExpect(content().string("Importação de planilha iniciada!"));

        verify(planilhaService).importarPlanilha();
    }
}