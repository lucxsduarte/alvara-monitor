package com.empresa.contabilidade.alvara_monitor.services;

import com.empresa.contabilidade.alvara_monitor.config.RabbitMQConfig;
import com.empresa.contabilidade.alvara_monitor.entities.Company;
import com.empresa.contabilidade.alvara_monitor.repositories.NotificationSettingRepository;
import com.empresa.contabilidade.alvara_monitor.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
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
public class NotificationService {

    private final CompanyRepository companyRepository;

    private final NotificationSettingRepository configuracaoRepository;

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.notifications.enabled:false}")
    private boolean notificationsEnabled;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    private void processNotificationRequest(final String message) {
        log.info("Mensagem recebida da fila: '{}'. Iniciando verificação de alvarás.", message);
        this.checkAndSendsAlerts();
    }

    private void checkAndSendsAlerts() {
        var configuracoesOpt = configuracaoRepository.findAll().stream().findFirst();
        if (configuracoesOpt.isEmpty() || configuracoesOpt.get().getRecipientEmails().isEmpty()) {
            log.warn("Nenhuma configuração de notificação ou e-mail de destino encontrado. A tarefa não será executada.");
            return;
        }

        var config = configuracoesOpt.get();
        var alertDays = config.getAlertDays();

        if (alertDays.isEmpty()) {
            log.warn("Nenhum dia de alerta configurado. A tarefa não será executada.");
            return;
        }

        final var datesToSearch = alertDays.stream()
                .map(dias -> LocalDate.now().plusDays(dias))
                .toList();

        final var companiesExpiring = companyRepository.findExpLicensesOnDates(datesToSearch);

        if (!companiesExpiring.isEmpty()) {
            sendSummaryEmail(config.getRecipientEmails(), companiesExpiring, datesToSearch);
        } else {
            log.warn("Nenhum alvará vencendo nos prazos configurados. Nenhum e-mail enviado.");
        }
    }

    private void sendSummaryEmail(final List<String> recipients, final List<Company> companies, final List<LocalDate> expDates) {
        if (!notificationsEnabled) {
            log.warn("O envio de notificações está desabilitado neste ambiente. Nenhum e-mail será enviado.");
            return;
        }

        if (Objects.isNull(recipients) || recipients.isEmpty()) {
            log.warn("Tentativa de envio de e-mail de notificação, mas não há destinatários configurados.");
            return;
        }

        try {
            final var message = mailSender.createMimeMessage();
            final var helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("seu-email@gmail.com"); // Será substituído pela variável de ambiente
            helper.setTo(recipients.toArray(new String[0]));
            helper.setSubject("Alerta de Vencimento de Alvarás");

            final var htmlMsg = formatEmail(companies, expDates);
            helper.setText(htmlMsg, true);

            mailSender.send(message);
            log.info("E-mail de notificação enviado com sucesso!");
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de notificação. Destinatários: {}. Erro: {}",
                    String.join(", ", recipients),
                    e.getMessage());
        }
    }

    private String formatEmail(final List<Company> companies, final List<LocalDate> expDates) {
        final var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        final var html = new StringBuilder("<html><body>")
                .append("<h2>Olá,</h2>")
                .append("<p>Os seguintes alvarás estão próximos do vencimento:</p>")
                .append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>")
                .append("<thead><tr><th>Empresa</th><th>Tipo de Alvará</th><th>Data de Vencimento</th></tr></thead>")
                .append("<tbody>");

        for (var comp : companies) {
            if (expDates.contains(comp.getExpLicenseFiredept())) {
                html.append(String.format("<tr><td>%s</td><td>Bombeiros</td><td>%s</td></tr>", comp.getName(), comp.getExpLicenseFiredept().format(formatter)));
            }

            if (expDates.contains(comp.getExpLicenseOperating())) {
                html.append(String.format("<tr><td>%s</td><td>Funcionamento</td><td>%s</td></tr>", comp.getName(), comp.getExpLicenseOperating().format(formatter)));
            }

            if (expDates.contains(comp.getExpLicensePolice())) {
                html.append(String.format("<tr><td>%s</td><td>Polícia Civil</td><td>%s</td></tr>", comp.getName(), comp.getExpLicensePolice().format(formatter)));
            }

            if (expDates.contains(comp.getExpLicenseSurveillance())) {
                html.append(String.format("<tr><td>%s</td><td>Vigilância Sanitária</td><td>%s</td></tr>", comp.getName(), comp.getExpLicenseSurveillance().format(formatter)));
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
