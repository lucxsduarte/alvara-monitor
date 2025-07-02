package com.empresa.contabilidade.alvara_monitor.service;

import com.empresa.contabilidade.alvara_monitor.model.Empresa;
import com.empresa.contabilidade.alvara_monitor.repository.EmpresaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanilhaService {

    private final EmpresaRepository empresaRepository;
    private final CsvDataProvider csvDataProvider;

    @Value("${planilha.url}")
    private String planilhaUrl;

    @Transactional
    public void importarPlanilha() {
        log.info("Iniciando leitura da planilha de empresas...");

        try (var reader = new BufferedReader(new InputStreamReader(csvDataProvider.lerDadosDaUrl(planilhaUrl)))) {
            var linha = "";
            var primeiraLinha = true;
            final var listaEmpresas = new ArrayList<Empresa>();
            final var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            final var padraoData = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");

            while ((linha = reader.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                final var colunas = linha.split(",");
                final var nome = colunas[0].trim();

                final var vencBombeiros = parseData(colunas, 1, formatter, padraoData);
                final var vencVigilantes = parseData(colunas, 2, formatter, padraoData);
                final var vencPolicia = parseData(colunas, 3, formatter, padraoData);
                final var vencFuncionamento = parseData(colunas, 4, formatter, padraoData);

                if (!empresaRepository.existsByNome(nome)) {
                    final var empresa = new Empresa();
                    empresa.setNome(nome);
                    empresa.setVencBombeiros(vencBombeiros);
                    empresa.setVencVigilancia(vencVigilantes);
                    empresa.setVencPolicia(vencPolicia);
                    empresa.setVencFuncionamento(vencFuncionamento);

                    empresaRepository.save(empresa);
                    listaEmpresas.add(empresa);
                }
            }

            log.info("Importação concluída com sucesso. Total de empresas importadas: {}", listaEmpresas.size());
        } catch (Exception e) {
            log.error("Erro ao importar a planilha: ", e);
        }
    }

    private LocalDate parseData(final String[] colunas, final Integer index, final DateTimeFormatter formatter, final Pattern padraoData) {
        if (colunas.length > index) {
            final var valor = colunas[index].trim();
            if (valor.isEmpty() || !padraoData.matcher(valor).matches()) {
                return null;
            }

            try {
                return LocalDate.parse(valor, formatter);
            } catch (DateTimeParseException e) {
                log.info("Não foi possível converter a data do index: {}", index, e);
                return null;
            }
        }
        return null;
    }

}
