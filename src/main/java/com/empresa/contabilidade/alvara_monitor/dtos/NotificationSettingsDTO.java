package com.empresa.contabilidade.alvara_monitor.dtos;

import java.util.List;

public record NotificationSettingsDTO(
        List<Integer> alertDays,
        List<String> recipientEmails
) {
}
