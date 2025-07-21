package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final NotificacaoService service;

    @PostMapping("/trigger-notifications")
    public ResponseEntity<String> triggerNotifications() {
        service.verificarEEnviarAlertas();
        return ResponseEntity.ok("Tarefa de notificação acionada com sucesso.");
    }
}
