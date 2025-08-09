package com.empresa.contabilidade.alvara_monitor.repositories;

import com.empresa.contabilidade.alvara_monitor.entities.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
}