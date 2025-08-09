package com.empresa.contabilidade.alvara_monitor.services;

import com.empresa.contabilidade.alvara_monitor.dtos.NotificationSettingsDTO;
import com.empresa.contabilidade.alvara_monitor.entities.NotificationSetting;
import com.empresa.contabilidade.alvara_monitor.repositories.NotificationSettingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class NotificationSettingsService {

    private final NotificationSettingRepository repository;

    @Transactional
    public NotificationSettingsDTO getSettings() {
        final var allSettings = repository.findAll();
        if (allSettings.isEmpty()) {
            final var newSettings = repository.save(new NotificationSetting(null, Collections.emptyList(), Collections.emptyList()));
            return toDTO(newSettings);
        }

        return toDTO(allSettings.getFirst());
    }

    @Transactional
    public NotificationSettingsDTO updateSettings(final NotificationSettingsDTO dto) {
        final var allSettings = repository.findAll();
        var settingsToUpdate = new NotificationSetting();

        if (!allSettings.isEmpty()) {
            settingsToUpdate = allSettings.getFirst();
        }

        settingsToUpdate.setAlertDays(dto.alertDays());
        settingsToUpdate.setRecipientEmails(dto.recipientEmails());

        return toDTO(repository.save(settingsToUpdate));
    }

    private NotificationSettingsDTO toDTO(final NotificationSetting entity) {
        return new NotificationSettingsDTO(entity.getAlertDays(), entity.getRecipientEmails());
    }
}
