package com.empresa.contabilidade.alvara_monitor.services;

import com.auth0.jwt.JWT;
import com.empresa.contabilidade.alvara_monitor.entities.User;
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
    void shouldGenerateValidJwtTokenForUser() {
        final var user = new User(1L, "teste", "senha", "ROLE_USER");

        final var generatedToken = tokenService.generateToken(user);

        assertNotNull(generatedToken);

        final var decodedJWT = JWT.decode(generatedToken);
        assertEquals("API Alvara.monitor", decodedJWT.getIssuer());
        assertEquals("teste", decodedJWT.getSubject());
    }

    @Test
    @DisplayName("Deve extrair o subject (login) corretamente de um token válido")
    void shouldExtractSubjectCorrectlyFromValidToken() {
        final var usuario = new User(1L, "teste", "senha", "ROLE_USER");
        final var tokenGerado = tokenService.generateToken(usuario);

        final var subject = tokenService.getSubject(tokenGerado);

        assertNotNull(subject);
        assertEquals("teste", subject);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para um token JWT inválido ou malformado")
    void shouldThrowExceptionForInvalidOrMalformedToken() {
        final var invalidToken = "um.token.falsificado";

        final var exception = assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject(invalidToken);
        });

        assertEquals("Token JWT inválido ou expirado!", exception.getMessage());
    }
}