package com.empresa.contabilidade.alvara_monitor.service;

import com.empresa.contabilidade.alvara_monitor.enums.FiltroStatusEmpresa;
import com.empresa.contabilidade.alvara_monitor.exception.BusinessException;
import com.empresa.contabilidade.alvara_monitor.exception.ResourceNotFoundException;
import com.empresa.contabilidade.alvara_monitor.model.Empresa;
import com.empresa.contabilidade.alvara_monitor.repository.EmpresaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public List<Empresa> listar(final String nome, final FiltroStatusEmpresa status) {
        if (StringUtils.hasText(nome)) {
            return empresaRepository.findByNomeContainingIgnoreCase(nome);
        }

        if (FiltroStatusEmpresa.VENCIDOS.equals(status)) {
            return empresaRepository.findComAlvarasVencidos(LocalDate.now());
        }

        return empresaRepository.findAll();
    }

    public Empresa buscarPorId(final Long id) {
        return empresaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o id: " + id));
    }

    @Transactional
    public Empresa salvar(final Empresa empresa) {
        validarEmpresa(empresa);
        return empresaRepository.save(empresa);
    }

    @Transactional
    public Empresa atualizar(final Long id, final Empresa empresa) {
        final var empresaExistente = buscarPorId(id);

        validarEmpresa(empresa);
        empresaExistente.setNome(empresa.getNome());
        empresaExistente.setVencBombeiros(empresa.getVencBombeiros());
        empresaExistente.setVencFuncionamento(empresa.getVencFuncionamento());
        empresaExistente.setVencPolicia(empresa.getVencPolicia());
        empresaExistente.setVencVigilancia(empresa.getVencVigilancia());

        return empresaRepository.save(empresaExistente);
    }

    @Transactional
    public void deletar(final Long id) {
        buscarPorId(id);
        empresaRepository.deleteById(id);
    }

    private void validarEmpresa(Empresa empresa) {
        if (!StringUtils.hasText(empresa.getNome())) {
            throw new BusinessException("O nome da empresa não pode ser vazio.");
        }

        if (Objects.isNull(empresa.getVencPolicia())
                && Objects.isNull(empresa.getVencBombeiros())
                && Objects.isNull(empresa.getVencVigilancia())
                && Objects.isNull(empresa.getVencFuncionamento())) {
            throw new BusinessException("É necessário preencher a data de vencimento de pelo menos um alvará.");
        }
    }
}
