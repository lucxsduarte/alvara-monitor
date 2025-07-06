package com.empresa.contabilidade.alvara_monitor.repository.impl;

import com.empresa.contabilidade.alvara_monitor.dto.AlvaraVencendoDTO;
import com.empresa.contabilidade.alvara_monitor.model.Empresa;
import com.empresa.contabilidade.alvara_monitor.repository.EmpresaRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class EmpresaRepositoryCustomImpl implements EmpresaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long countAlvarasVencidos(final LocalDate dataReferencia) {
        final var nativeSql = "SELECT COUNT(*) FROM ( " +
                "SELECT 1 FROM empresas WHERE venc_bombeiros < ? UNION ALL " +
                "SELECT 1 FROM empresas WHERE venc_vigilancia < ? UNION ALL " +
                "SELECT 1 FROM empresas WHERE venc_policia < ? UNION ALL " +
                "SELECT 1 FROM empresas WHERE venc_funcionamento < ? " +
                ") AS vencidos";

        final var query = entityManager.createNativeQuery(nativeSql);
        query.setParameter(1, dataReferencia);
        query.setParameter(2, dataReferencia);
        query.setParameter(3, dataReferencia);
        query.setParameter(4, dataReferencia);

        final var result = query.getSingleResult();
        return (result instanceof Number) ? ((Number) result).longValue() : 0L;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AlvaraVencendoDTO> findAlvarasVencendoNoPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        final var nativeSql =
                "SELECT id, nome, 'Bombeiros' as tipo_alvara, venc_bombeiros as data_vencimento FROM empresas WHERE venc_bombeiros BETWEEN ? AND ? " +
                        "UNION ALL " +
                        "SELECT id, nome, 'Vigilância Sanitária' as tipo_alvara, venc_vigilancia as data_vencimento FROM empresas WHERE venc_vigilancia BETWEEN ? AND ? " +
                        "UNION ALL " +
                        "SELECT id, nome, 'Polícia Civil' as tipo_alvara, venc_policia as data_vencimento FROM empresas WHERE venc_policia BETWEEN ? AND ? " +
                        "UNION ALL " +
                        "SELECT id, nome, 'Funcionamento' as tipo_alvara, venc_funcionamento as data_vencimento FROM empresas WHERE venc_funcionamento BETWEEN ? AND ?";

        final var query = entityManager.createNativeQuery(nativeSql);
        query.setParameter(1, dataInicio);
        query.setParameter(2, dataFim);
        query.setParameter(3, dataInicio);
        query.setParameter(4, dataFim);
        query.setParameter(5, dataInicio);
        query.setParameter(6, dataFim);
        query.setParameter(7, dataInicio);
        query.setParameter(8, dataFim);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(row -> new AlvaraVencendoDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        (row[3] instanceof Date) ? ((Date) row[3]).toLocalDate() : null
                ))
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Empresa> findComAlvarasVencidos(LocalDate dataReferencia) {
        final var nativeSql = "SELECT * FROM empresas e WHERE " +
                "e.venc_bombeiros < ? OR " +
                "e.venc_vigilancia < ? OR " +
                "e.venc_policia < ? OR " +
                "e.venc_funcionamento < ?";


        final var query = entityManager.createNativeQuery(nativeSql, Empresa.class);
        query.setParameter(1, dataReferencia);
        query.setParameter(2, dataReferencia);
        query.setParameter(3, dataReferencia);
        query.setParameter(4, dataReferencia);

        return query.getResultList();
    }
}
