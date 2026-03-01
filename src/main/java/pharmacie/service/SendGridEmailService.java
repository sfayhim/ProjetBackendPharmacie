package pharmacie.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SendGridEmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;

    public void sendEmail(String toEmail, String subject, String body) {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("✅ Email envoyé avec succès à {} via SendGrid (status: {})", toEmail, response.getStatusCode());
            } else {
                log.error("❌ Erreur lors de l'envoi de l'email à {} via SendGrid. Status: {}, Body: {}", 
                    toEmail, response.getStatusCode(), response.getBody());
            }
        } catch (IOException e) {
            log.error("❌ Exception lors de l'envoi de l'email à {} via SendGrid: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email via SendGrid", e);
        }
    }
}
