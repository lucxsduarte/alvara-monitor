package com.empresa.contabilidade.alvara_monitor.config;

import com.empresa.contabilidade.alvara_monitor.model.Usuario;
import com.empresa.contabilidade.alvara_monitor.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.initial-admin.login}")
    private String adminLogin;

    @Value("${app.initial-admin.senha}")
    private String adminSenha;

    @Override
    public void run(String... args) {
        if (StringUtils.hasText(adminLogin) && StringUtils.hasText(adminSenha)) {
            if (usuarioRepository.findByLogin(adminLogin) == null) {
                log.info("Nenhum usuário administrador inicial encontrado, criando um novo...");

                var adminUser = new Usuario(
                        adminLogin,
                        passwordEncoder.encode(adminSenha),
                        "ROLE_ADMIN"
                );

                usuarioRepository.save(adminUser);
                log.info("Usuário administrador '{}' criado com sucesso.", adminLogin);
            } else {
                log.info("Usuário administrador '{}' já existe.", adminLogin);
            }
        } else {
            log.warn("Credenciais do administrador inicial não definidas nas variáveis de ambiente. Nenhum usuário admin será criado.");
        }
    }
}