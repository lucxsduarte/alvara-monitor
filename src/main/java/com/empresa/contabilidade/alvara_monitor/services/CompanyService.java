package com.empresa.contabilidade.alvara_monitor.services;

import com.empresa.contabilidade.alvara_monitor.enums.companyStatusFilter;
import com.empresa.contabilidade.alvara_monitor.exceptions.BusinessException;
import com.empresa.contabilidade.alvara_monitor.exceptions.ResourceNotFoundException;
import com.empresa.contabilidade.alvara_monitor.entities.Company;
import com.empresa.contabilidade.alvara_monitor.repositories.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<Company> listCompanies(final String nome, final companyStatusFilter status) {
        if (StringUtils.hasText(nome)) {
            return companyRepository.findByNameContainingIgnoreCase(nome);
        }

        if (companyStatusFilter.VENCIDOS.equals(status)) {
            return companyRepository.findExpLicenses(LocalDate.now());
        }

        return companyRepository.findAll();
    }

    public Company findById(final Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o id: " + id));
    }

    @Transactional
    public Company save(final Company company) {
        validateCompany(company);
        return companyRepository.save(company);
    }

    @Transactional
    public Company update(final Long id, final Company company) {
        final var empresaExistente = findById(id);

        validateCompany(company);
        empresaExistente.setName(company.getName());
        empresaExistente.setExpLicenseFiredept(company.getExpLicenseFiredept());
        empresaExistente.setExpLicenseOperating(company.getExpLicenseOperating());
        empresaExistente.setExpLicensePolice(company.getExpLicensePolice());
        empresaExistente.setExpLicenseSurveillance(company.getExpLicenseSurveillance());

        return companyRepository.save(empresaExistente);
    }

    @Transactional
    public void delete(final Long id) {
        findById(id);
        companyRepository.deleteById(id);
    }

    private void validateCompany(Company company) {
        if (!StringUtils.hasText(company.getName())) {
            throw new BusinessException("O nome da empresa não pode ser vazio.");
        }

        if (Objects.isNull(company.getExpLicensePolice())
                && Objects.isNull(company.getExpLicenseFiredept())
                && Objects.isNull(company.getExpLicenseSurveillance())
                && Objects.isNull(company.getExpLicenseOperating())) {
            throw new BusinessException("É necessário preencher a data de vencimento de pelo menos um alvará.");
        }
    }
}
