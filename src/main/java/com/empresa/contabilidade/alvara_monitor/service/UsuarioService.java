package com.empresa.contabilidade.alvara_monitor.service;

import com.empresa.contabilidade.alvara_monitor.dto.CriarUsuarioDTO;
import com.empresa.contabilidade.alvara_monitor.dto.EditarUsuarioDTO;
import com.empresa.contabilidade.alvara_monitor.dto.UsuarioDTO;
import com.empresa.contabilidade.alvara_monitor.exception.BusinessException;
import com.empresa.contabilidade.alvara_monitor.exception.ResourceNotFoundException;
import com.empresa.contabilidade.alvara_monitor.model.Usuario;
import com.empresa.contabilidade.alvara_monitor.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public UsuarioDTO criarNovoUsuario(final CriarUsuarioDTO dados) {
        if (Objects.nonNull(usuarioRepository.findByLogin(dados.login()))) {
            throw new BusinessException("Login já existente.");
        }

        final var senhaCriptografada = passwordEncoder.encode(dados.password());

        final var novoUsuario = new Usuario(
                dados.login(),
                senhaCriptografada,
                dados.role()
        );

        final var usuarioSalvo = usuarioRepository.save(novoUsuario);
        return toDTO(usuarioSalvo);
    }

    @Transactional
    public UsuarioDTO atualizar(final Long id, final EditarUsuarioDTO dados) {
        final var user = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        final var usuarioComNovoLogin = usuarioRepository.findByLogin(dados.login());
        if (Objects.nonNull(usuarioComNovoLogin) && !usuarioComNovoLogin.getId().equals(id)) {
            throw new BusinessException("Login já está em uso por outro usuário.");
        }

        user.setLogin(dados.login());
        user.setRole(dados.role());
        return toDTO(user);
    }

    @Transactional
    public void deletar(final Long id) {
        final var user = usuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        usuarioRepository.delete(user);
    }

    private UsuarioDTO toDTO(final Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getAuthorities().iterator().next().getAuthority()
        );
    }
}