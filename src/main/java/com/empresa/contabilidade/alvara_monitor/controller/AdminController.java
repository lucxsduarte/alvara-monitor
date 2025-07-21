package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.dto.ConfiguracaoNotificacaoDTO;
import com.empresa.contabilidade.alvara_monitor.dto.CriarUsuarioDTO;
import com.empresa.contabilidade.alvara_monitor.dto.EditarUsuarioDTO;
import com.empresa.contabilidade.alvara_monitor.dto.UsuarioDTO;
import com.empresa.contabilidade.alvara_monitor.service.ConfiguracaoNotificacaoService;
import com.empresa.contabilidade.alvara_monitor.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioService usuarioService;

    private final ConfiguracaoNotificacaoService configuracaoNotificacaoService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/notificacoes/configuracoes")
    public ResponseEntity<ConfiguracaoNotificacaoDTO> getNotificationSettings() {
        var settings = configuracaoNotificacaoService.getSettings();
        return ResponseEntity.ok(settings);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioDTO> criarUsuario(@RequestBody @Valid final CriarUsuarioDTO dados) {
        var usuarioCriado = usuarioService.criarNovoUsuario(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @RequestBody @Valid EditarUsuarioDTO dados) {
        var usuarioAtualizado = usuarioService.atualizar(id, dados);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @PutMapping("/notificacoes/configuracoes")
    public ResponseEntity<ConfiguracaoNotificacaoDTO> updateNotificationSettings(@RequestBody ConfiguracaoNotificacaoDTO dto) {
        var updatedSettings = configuracaoNotificacaoService.updateSettings(dto);
        return ResponseEntity.ok(updatedSettings);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}