package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.dtos.AuthenticationDataDTO;
import com.empresa.contabilidade.alvara_monitor.entities.User;
import com.empresa.contabilidade.alvara_monitor.repositories.UserRepository;
import com.empresa.contabilidade.alvara_monitor.services.TokenService;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(AuthenticationControllerTest.TestConfig.class)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class AuthenticationControllerTest {

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
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }
    }

    @BeforeEach
    void setup() {
        Mockito.reset(authenticationManager, tokenService);
    }

    @Test
    @DisplayName("Deve retornar status 200 OK e um token JWT ao logar com credenciais válidas")
    void shouldReturnOkAndTokenForValidCredentials() throws Exception {
        final var loginData  = new AuthenticationDataDTO("admin", "123");
        final var user = new User(1L, "admin", "senha-criptografada", "ROLE_USER");
        final var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.generateToken(any(User.class))).thenReturn("token.jwt.simulado.valido");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginData )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token.jwt.simulado.valido"));
    }

    @Test
    @DisplayName("Deve retornar status 401 Unauthorized ao logar com credenciais inválidas")
    void shouldReturnUnauthorizedForInvalidCredentials() throws Exception {
        final var loginData = new AuthenticationDataDTO("admin", "senha-errada");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isForbidden());
    }
}