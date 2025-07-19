package com.empresa.contabilidade.alvara_monitor.service;

import com.auth0.jwt.JWT;
import com.empresa.contabilidade.alvara_monitor.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TokenService.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {"api.security.token.secret=nosso-segredo-de-teste"})
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Test
    @DisplayName("Deve gerar um token JWT válido para um usuário")
    void gerarTokenValido() {
        final var usuario = new Usuario(1L, "teste", "senha", "ROLE_USER");

        final var tokenGerado = tokenService.gerarToken(usuario);

        assertNotNull(tokenGerado);

        final var decodedJWT = JWT.decode(tokenGerado);
        assertEquals("API Alvara.monitor", decodedJWT.getIssuer());
        assertEquals("teste", decodedJWT.getSubject());
    }

    @Test
    @DisplayName("Deve extrair o subject (login) corretamente de um token válido")
    void extrairSubjectTokenValido() {
        final var usuario = new Usuario(1L, "teste", "senha", "ROLE_USER");
        final var tokenGerado = tokenService.gerarToken(usuario);

        final var subject = tokenService.getSubject(tokenGerado);

        assertNotNull(subject);
        assertEquals("teste", subject);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para um token JWT inválido ou malformado")
    void lancarExcecaoTokenInvalido() {
        final var tokenInvalido = "um.token.falsificado";

        final var exception = assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject(tokenInvalido);
        });

        assertEquals("Token JWT inválido ou expirado!", exception.getMessage());
    }
}