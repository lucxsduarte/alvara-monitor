package com.empresa.contabilidade.alvara_monitor.service;

import com.empresa.contabilidade.alvara_monitor.model.Empresa;
import com.empresa.contabilidade.alvara_monitor.repository.ConfiguracaoNotificacaoRepository;
import com.empresa.contabilidade.alvara_monitor.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacaoService {

    private final EmpresaRepository empresaRepository;

    private final ConfiguracaoNotificacaoRepository configuracaoRepository;

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private final String frontendUrl;

    @Value("${app.notifications.enabled:false}")
    private boolean notificationsEnabled;

    public void verificarEEnviarAlertas() {
        var configuracoesOpt = configuracaoRepository.findAll().stream().findFirst();
        if (configuracoesOpt.isEmpty() || configuracoesOpt.get().getEmailsDestino().isEmpty()) {
            log.warn("Nenhuma configuração de notificação ou e-mail de destino encontrado. A tarefa não será executada.");
            return;
        }

        var config = configuracoesOpt.get();
        var diasDeAlerta = config.getDiasAlerta();

        if (diasDeAlerta.isEmpty()) {
            log.warn("Nenhum dia de alerta configurado. A tarefa não será executada.");
            return;
        }

        final var datasParaBuscar = diasDeAlerta.stream()
                .map(dias -> LocalDate.now().plusDays(dias))
                .toList();

        final var empresasComVencimentos = empresaRepository.findEmpresasComAlvaraVencendoEmDatas(datasParaBuscar);

        if (!empresasComVencimentos.isEmpty()) {
            enviarEmailDeResumo(config.getEmailsDestino(), empresasComVencimentos, datasParaBuscar);
        } else {
            log.warn("Nenhum alvará vencendo nos prazos configurados. Nenhum e-mail enviado.");
        }
    }

    private void enviarEmailDeResumo(final List<String> destinatarios, final List<Empresa> empresas, final List<LocalDate> datasDeVencimento) {
        if (!notificationsEnabled) {
            log.warn("O envio de notificações está desabilitado neste ambiente. Nenhum e-mail será enviado.");
            return;
        }

        if (Objects.isNull(destinatarios) || destinatarios.isEmpty()) {
            log.warn("Tentativa de envio de e-mail de notificação, mas não há destinatários configurados.");
            return;
        }

        try {
            final var message = mailSender.createMimeMessage();
            final var helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("seu-email@gmail.com"); // Será substituído pela variável de ambiente
            helper.setTo(destinatarios.toArray(new String[0]));
            helper.setSubject("Alerta de Vencimento de Alvarás");

            final var htmlMsg = formatarEmailHtml(empresas, datasDeVencimento);
            helper.setText(htmlMsg, true);

            mailSender.send(message);
            log.info("E-mail de notificação enviado com sucesso!");
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de notificação. Destinatários: {}. Erro: {}",
                    String.join(", ", destinatarios),
                    e.getMessage());
        }
    }

    private String formatarEmailHtml(final List<Empresa> empresas, final List<LocalDate> datasDeVencimento) {
        final var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        final var html = new StringBuilder("<html><body>")
                .append("<h2>Olá,</h2>")
                .append("<p>Os seguintes alvarás estão próximos do vencimento:</p>")
                .append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>")
                .append("<thead><tr><th>Empresa</th><th>Tipo de Alvará</th><th>Data de Vencimento</th></tr></thead>")
                .append("<tbody>");

        for (var empresa : empresas) {
            if (datasDeVencimento.contains(empresa.getVencBombeiros())) {
                html.append(String.format("<tr><td>%s</td><td>Bombeiros</td><td>%s</td></tr>", empresa.getNome(), empresa.getVencBombeiros().format(formatter)));
            }

            if (datasDeVencimento.contains(empresa.getVencFuncionamento())) {
                html.append(String.format("<tr><td>%s</td><td>Funcionamento</td><td>%s</td></tr>", empresa.getNome(), empresa.getVencFuncionamento().format(formatter)));
            }

            if (datasDeVencimento.contains(empresa.getVencPolicia())) {
                html.append(String.format("<tr><td>%s</td><td>Polícia Civil</td><td>%s</td></tr>", empresa.getNome(), empresa.getVencPolicia().format(formatter)));
            }

            if (datasDeVencimento.contains(empresa.getVencVigilancia())) {
                html.append(String.format("<tr><td>%s</td><td>Vigilância Sanitária</td><td>%s</td></tr>", empresa.getNome(), empresa.getVencVigilancia().format(formatter)));
            }
        }

        html.append("</tbody></table>")
                .append("<p style='margin-top: 20px;'>")
                .append(String.format("<a href='%s/empresas' style='background-color: #2c3e50; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px;'>Ver Empresas</a>", frontendUrl))
                .append("</p>")
                .append("<p>Atenciosamente,<br/>Sistema de Monitoramento de Alvarás</p>")
                .append("</body></html>");

        return html.toString();
    }
}
