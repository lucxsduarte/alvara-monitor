package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.dto.DadosAutenticacaoDTO;
import com.empresa.contabilidade.alvara_monitor.model.Usuario;
import com.empresa.contabilidade.alvara_monitor.repository.UsuarioRepository;
import com.empresa.contabilidade.alvara_monitor.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutenticacaoController.class)
@Import(AutenticacaoControllerTest.TestConfig.class)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public AuthenticationManager authenticationManager() {
            return Mockito.mock(AuthenticationManager.class);
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

    @BeforeEach
    void setup() {
        Mockito.reset(authenticationManager, tokenService);
    }

    @Test
    @DisplayName("Deve retornar status 200 OK e um token JWT ao logar com credenciais válidas")
    void deveRetornarOkETokenParaCredenciaisValidas() throws Exception {
        final var dadosLogin = new DadosAutenticacaoDTO("admin", "123");
        final var usuario = new Usuario(1L, "admin", "senha-criptografada", "ROLE_USER");
        final var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.gerarToken(any(Usuario.class))).thenReturn("token.jwt.simulado.valido");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token.jwt.simulado.valido"));
    }

    @Test
    @DisplayName("Deve retornar status 401 Unauthorized ao logar com credenciais inválidas")
    void deveRetornarUnauthorizedParaCredenciaisInvalidas() throws Exception {
        final var dadosLogin = new DadosAutenticacaoDTO("admin", "senha-errada");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosLogin)))
                .andExpect(status().isForbidden());
    }
}