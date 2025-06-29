package com.empresa.contabilidade.alvara_monitor.repository;

import com.empresa.contabilidade.alvara_monitor.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaRepository extends JpaRepository<Empresa, Long>, EmpresaRepositoryCustom {

    boolean existsByNome(String nome);

    List<Empresa> findByNomeContainingIgnoreCase(String nome);
}
