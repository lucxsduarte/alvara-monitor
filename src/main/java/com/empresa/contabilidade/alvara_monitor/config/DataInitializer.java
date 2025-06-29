package com.empresa.contabilidade.alvara_monitor.config;

import com.empresa.contabilidade.alvara_monitor.model.Usuario;
import com.empresa.contabilidade.alvara_monitor.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (Objects.isNull(usuarioRepository.findByLogin("admin"))) {
            log.info("Nenhum usuário 'admin' encontrado, criando um novo...");

            var adminUser = new Usuario(
                    null,
                    "admin",
                    passwordEncoder.encode("123")
            );

            usuarioRepository.save(adminUser);
            log.info("Usuário 'admin' criado com sucesso com a senha '123'");
        } else {
            log.info("Usuário 'admin' já existe no banco de dados. Nenhuma ação necessária.");
        }
    }
}