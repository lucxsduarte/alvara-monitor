package com.empresa.contabilidade.alvara_monitor.services;

import com.empresa.contabilidade.alvara_monitor.entities.Company;
import com.empresa.contabilidade.alvara_monitor.repositories.CompanyRepository;
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
public class SpreadsheetService {

    private final CompanyRepository companyRepository;
    private final CsvDataProvider csvDataProvider;

    @Value("${planilha.url}")
    private String spreadsheetUrl;

    @Transactional
    public void importSpreadsheet() {
        log.info("Iniciando leitura da planilha de empresas...");

        try (var reader = new BufferedReader(new InputStreamReader(csvDataProvider.readDataFromUrl(spreadsheetUrl)))) {
            var line = "";
            var firstLine = true;
            final var companiesList = new ArrayList<Company>();
            final var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            final var datePattern = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                final var columns = line.split(",");
                final var name = columns[0].trim();

                final var expLicenseFiredept = parseData(columns, 1, formatter, datePattern);
                final var expLicenseSurveillance = parseData(columns, 2, formatter, datePattern);
                final var expLicensePolice = parseData(columns, 3, formatter, datePattern);
                final var expLicenseOperating = parseData(columns, 4, formatter, datePattern);

                if (!companyRepository.existsByName(name)) {
                    final var company = new Company();
                    company.setName(name);
                    company.setExpLicenseFiredept(expLicenseFiredept);
                    company.setExpLicenseSurveillance(expLicenseSurveillance);
                    company.setExpLicensePolice(expLicensePolice);
                    company.setExpLicenseOperating(expLicenseOperating);

                    companyRepository.save(company);
                    companiesList.add(company);
                }
            }

            log.info("Importação concluída com sucesso. Total de empresas importadas: {}", companiesList.size());
        } catch (Exception e) {
            log.error("Erro ao importar a planilha: ", e);
        }
    }

    private LocalDate parseData(final String[] columns, final Integer index, final DateTimeFormatter formatter, final Pattern datePattern) {
        if (columns.length > index) {
            final var value = columns[index].trim();
            if (value.isEmpty() || !datePattern.matcher(value).matches()) {
                return null;
            }

            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException e) {
                log.info("Não foi possível converter a data do index: {}", index, e);
                return null;
            }
        }
        return null;
    }

}
