package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.dto.AlvaraVencendoDTO;
import com.empresa.contabilidade.alvara_monitor.dto.DashboardSummaryDTO;
import com.empresa.contabilidade.alvara_monitor.repository.UsuarioRepository;
import com.empresa.contabilidade.alvara_monitor.service.DashboardService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(DashboardController.class)
@Import(DashboardControllerTest.TestConfig.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DashboardService dashboardService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public DashboardService dashboardService() {
            return Mockito.mock(DashboardService.class);

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
    @DisplayName("Deve retornar status 200 OK e o sumário do dashboard")
    @WithMockUser(roles = "USER")
    void deveRetornarOkComSumarioDoDashboard() throws Exception {
        var alvaraFake = new AlvaraVencendoDTO(1L, "Empresa Fake", "Bombeiros", LocalDate.now().plusDays(15));
        var summaryDTO = new DashboardSummaryDTO(150, 25, List.of(alvaraFake), Collections.emptyList());

        when(dashboardService.getSummary()).thenReturn(summaryDTO);

        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmpresas", is(150)))
                .andExpect(jsonPath("$.totalAlvarasVencidos", is(25)))
                .andExpect(jsonPath("$.alvarasVencendo30Dias[0].nomeEmpresa", is("Empresa Fake")));
    }
}