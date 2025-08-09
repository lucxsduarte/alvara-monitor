package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.services.SpreadsheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spreadsheet")
public class SpreadsheetController {

    private final SpreadsheetService spreadsheetService;

    @GetMapping("/import-spreadsheet")
    public String triggerManualImport() {
        spreadsheetService.importSpreadsheet();
        return "Importação de planilha iniciada!";
    }

}
