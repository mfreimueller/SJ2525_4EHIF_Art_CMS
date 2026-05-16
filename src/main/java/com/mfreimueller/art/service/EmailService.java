package com.mfreimueller.art.service;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final MeterRegistry meterRegistry;

    public void sendEmail(String to, String subject, String htmlBody) {
        var start = System.nanoTime();
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            var elapsed = System.nanoTime() - start;
            meterRegistry.timer("email.send.duration").record(elapsed, TimeUnit.NANOSECONDS);
            log.debug("Email sent to {} with subject '{}' (took {} ms)", to, subject, elapsed / 1_000_000);
        } catch (Exception e) {
            log.warn("Failed to send email to {} with subject '{}': {}", to, subject, e.getMessage());
        }
    }

    public void sendWelcomeEmail(String to, String username) {
        String subject = "Welcome to Art CMS!";
        String body = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="font-family: sans-serif; padding: 2rem;">
                    <h2>Welcome to Art CMS, <span th:text="%s">%s</span>!</h2>
                    <p>Your account has been created successfully. You can now log in and explore the art collection.</p>
                    <hr>
                    <footer style="color: #888; font-size: 0.8rem;">
                        <p>Art CMS &mdash; HTL Teaching Project</p>
                    </footer>
                </body>
                </html>
                """.formatted(username, username);
        sendEmail(to, subject, body);
    }

    public void sendNewContentNotification(String to, String contentType, String description, Long contentId) {
        String subject = "New Content Added: " + contentType;
        String body = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="font-family: sans-serif; padding: 2rem;">
                    <h2>New Content Notification</h2>
                    <p>A new <strong>%s</strong> has been added to Art CMS:</p>
                    <p><em>%s</em></p>
                    <p>Content ID: %d</p>
                    <hr>
                    <footer style="color: #888; font-size: 0.8rem;">
                        <p>Art CMS &mdash; HTL Teaching Project</p>
                    </footer>
                </body>
                </html>
                """.formatted(contentType, description, contentId);
        sendEmail(to, subject, body);
    }
}
