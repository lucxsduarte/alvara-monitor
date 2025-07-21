package com.empresa.contabilidade.alvara_monitor.repository;

import com.empresa.contabilidade.alvara_monitor.model.ConfiguracaoNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoNotificacaoRepository extends JpaRepository<ConfiguracaoNotificacao, Long> {
}