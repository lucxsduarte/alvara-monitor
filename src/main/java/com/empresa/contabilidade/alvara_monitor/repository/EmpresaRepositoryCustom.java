package com.empresa.contabilidade.alvara_monitor.repository;

import com.empresa.contabilidade.alvara_monitor.dto.AlvaraVencendoDTO;
import com.empresa.contabilidade.alvara_monitor.model.Empresa;

import java.time.LocalDate;
import java.util.List;

public interface EmpresaRepositoryCustom {

    long countAlvarasVencidos(LocalDate dataReferencia);

    List<AlvaraVencendoDTO> findAlvarasVencendoNoPeriodo(LocalDate dataInicio, LocalDate dataFim);

    List<Empresa> findComAlvarasVencidos(LocalDate dataReferencia);
}
