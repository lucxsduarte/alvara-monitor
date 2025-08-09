package com.empresa.contabilidade.alvara_monitor.config;

import com.empresa.contabilidade.alvara_monitor.entities.User;
import com.empresa.contabilidade.alvara_monitor.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.initial-admin.login}")
    private String adminLogin;

    @Value("${app.initial-admin.senha}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (StringUtils.hasText(adminLogin) && StringUtils.hasText(adminPassword)) {
            if (Objects.isNull(userRepository.findByLogin(adminLogin))) {
                log.info("Nenhum usuário administrador inicial encontrado, criando um novo...");

                var adminUser = new User(
                        adminLogin,
                        passwordEncoder.encode(adminPassword),
                        "ROLE_ADMIN"
                );

                userRepository.save(adminUser);
                log.info("Usuário administrador '{}' criado com sucesso.", adminLogin);
            } else {
                log.info("Usuário administrador '{}' já existe.", adminLogin);
            }
        } else {
            log.warn("Credenciais do administrador inicial não definidas nas variáveis de ambiente. Nenhum usuário admin será criado.");
        }
    }
}