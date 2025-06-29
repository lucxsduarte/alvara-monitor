package com.empresa.contabilidade.alvara_monitor.service;

import com.empresa.contabilidade.alvara_monitor.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final var user = usuarioRepository.findByLogin(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }

        return user;
    }
}
