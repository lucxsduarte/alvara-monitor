package com.empresa.contabilidade.alvara_monitor.repositories.impl;

import com.empresa.contabilidade.alvara_monitor.dtos.ExpiringLicenseDTO;
import com.empresa.contabilidade.alvara_monitor.entities.Company;
import com.empresa.contabilidade.alvara_monitor.repositories.CompanyRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CompanyRepositoryCustomImpl implements CompanyRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long countExpLicenses(final LocalDate referenceDate) {
        final var nativeSql = "SELECT COUNT(*) FROM ( " +
                "SELECT 1 FROM empresas WHERE venc_bombeiros < ? UNION ALL " +
                "SELECT 1 FROM empresas WHERE venc_vigilancia < ? UNION ALL " +
                "SELECT 1 FROM empresas WHERE venc_policia < ? UNION ALL " +
                "SELECT 1 FROM empresas WHERE venc_funcionamento < ? " +
                ") AS vencidos";

        final var query = entityManager.createNativeQuery(nativeSql);
        query.setParameter(1, referenceDate);
        query.setParameter(2, referenceDate);
        query.setParameter(3, referenceDate);
        query.setParameter(4, referenceDate);

        final var result = query.getSingleResult();
        return (result instanceof Number) ? ((Number) result).longValue() : 0L;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ExpiringLicenseDTO> findExpLicensesInterval(final LocalDate startDate, final LocalDate endDate) {
        final var nativeSql =
                "SELECT id, nome, 'Bombeiros' as tipo_alvara, venc_bombeiros as data_vencimento FROM empresas WHERE venc_bombeiros BETWEEN ? AND ? " +
                        "UNION ALL " +
                        "SELECT id, nome, 'Vigilância Sanitária' as tipo_alvara, venc_vigilancia as data_vencimento FROM empresas WHERE venc_vigilancia BETWEEN ? AND ? " +
                        "UNION ALL " +
                        "SELECT id, nome, 'Polícia Civil' as tipo_alvara, venc_policia as data_vencimento FROM empresas WHERE venc_policia BETWEEN ? AND ? " +
                        "UNION ALL " +
                        "SELECT id, nome, 'Funcionamento' as tipo_alvara, venc_funcionamento as data_vencimento FROM empresas WHERE venc_funcionamento BETWEEN ? AND ?";

        final var query = entityManager.createNativeQuery(nativeSql);
        query.setParameter(1, startDate);
        query.setParameter(2, endDate);
        query.setParameter(3, startDate);
        query.setParameter(4, endDate);
        query.setParameter(5, startDate);
        query.setParameter(6, endDate);
        query.setParameter(7, startDate);
        query.setParameter(8, endDate);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(row -> new ExpiringLicenseDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        (row[3] instanceof Date) ? ((Date) row[3]).toLocalDate() : null
                ))
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Company> findExpLicenses(final LocalDate referenceDate) {
        final var nativeSql = "SELECT * FROM empresas e " +
                "WHERE e.venc_bombeiros < ? " +
                "   OR e.venc_vigilancia < ? " +
                "   OR e.venc_policia < ? " +
                "   OR e.venc_funcionamento < ? ";


        final var query = entityManager.createNativeQuery(nativeSql, Company.class);
        query.setParameter(1, referenceDate);
        query.setParameter(2, referenceDate);
        query.setParameter(3, referenceDate);
        query.setParameter(4, referenceDate);

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Company> findExpLicensesOnDates(final List<LocalDate> dates) {
        if (Objects.isNull(dates) || dates.isEmpty()) {
            return Collections.emptyList();
        }

        final var placeholders = String.join(", ", Collections.nCopies(dates.size(), "?"));

        final var nativeSql = String.format(" SELECT * FROM empresas e " +
                        " WHERE e.venc_bombeiros IN (%s) " +
                        "    OR e.venc_funcionamento IN (%s) " +
                        "    OR e.venc_policia IN (%s) " +
                        "    OR e.venc_vigilancia IN (%s) ",
                placeholders, placeholders, placeholders, placeholders);

        final var query = entityManager.createNativeQuery(nativeSql, Company.class);

        int paramIndex = 1;
        for (int i = 0; i < 4; i++) {
            for (var data : dates) {
                query.setParameter(paramIndex++, data);
            }
        }

        return query.getResultList();
    }
}
