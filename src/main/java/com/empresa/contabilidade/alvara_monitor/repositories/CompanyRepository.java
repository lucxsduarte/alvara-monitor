package com.empresa.contabilidade.alvara_monitor.repositories;

import com.empresa.contabilidade.alvara_monitor.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long>, CompanyRepositoryCustom {

    boolean existsByName(String name);

    List<Company> findByNameContainingIgnoreCase(String name);
}
