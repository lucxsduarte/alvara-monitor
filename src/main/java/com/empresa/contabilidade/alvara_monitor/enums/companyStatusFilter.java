package com.empresa.contabilidade.alvara_monitor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum companyStatusFilter {
    VENCIDOS(0, "Vencidos");

    private final Integer codigo;
    private final String descricao;
}
