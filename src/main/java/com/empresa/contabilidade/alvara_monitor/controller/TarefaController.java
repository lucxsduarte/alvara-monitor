package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TarefaController {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping("/trigger-notifications")
    public ResponseEntity<String> triggerNotifications() {
        final var mensagem = "Verificar vencimentos de alvarás";
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, mensagem);

        return ResponseEntity.ok("Tarefa de notificação enviada para a fila de processamento.");
    }
}
