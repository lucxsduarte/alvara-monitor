package com.empresa.contabilidade.alvara_monitor.service;

import com.empresa.contabilidade.alvara_monitor.dto.ConfiguracaoNotificacaoDTO;
import com.empresa.contabilidade.alvara_monitor.model.ConfiguracaoNotificacao;
import com.empresa.contabilidade.alvara_monitor.repository.ConfiguracaoNotificacaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ConfiguracaoNotificacaoService {

    private final ConfiguracaoNotificacaoRepository repository;

    @Transactional
    public ConfiguracaoNotificacaoDTO getSettings() {
        final var allSettings = repository.findAll();
        if (allSettings.isEmpty()) {
            final var newSettings = repository.save(new ConfiguracaoNotificacao(null, Collections.emptyList(), Collections.emptyList()));
            return toDTO(newSettings);
        }

        return toDTO(allSettings.getFirst());
    }

    @Transactional
    public ConfiguracaoNotificacaoDTO updateSettings(final ConfiguracaoNotificacaoDTO dto) {
        final var allSettings = repository.findAll();
        var settingsToUpdate = new ConfiguracaoNotificacao();

        if (!allSettings.isEmpty()) {
            settingsToUpdate = allSettings.getFirst();
        }

        settingsToUpdate.setDiasAlerta(dto.diasAlerta());
        settingsToUpdate.setEmailsDestino(dto.emailsDestino());

        return toDTO(repository.save(settingsToUpdate));
    }

    private ConfiguracaoNotificacaoDTO toDTO(final ConfiguracaoNotificacao entity) {
        return new ConfiguracaoNotificacaoDTO(entity.getDiasAlerta(), entity.getEmailsDestino());
    }
}
