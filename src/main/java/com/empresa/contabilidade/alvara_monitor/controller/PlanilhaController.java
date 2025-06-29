package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.service.PlanilhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/planilha")
public class PlanilhaController {

    private final PlanilhaService planilhaService;

    @GetMapping("/importar-planilha")
    public String importarPlanilhaManual() {
        planilhaService.importarPlanilha();
        return "Importação de planilha iniciada!";
    }

}
