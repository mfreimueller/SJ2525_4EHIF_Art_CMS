package com.mfreimueller.art.service;

import ch.martinelli.oss.testcontainers.mailpit.MailpitContainer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmailServiceTest {

    @Container
    static MailpitContainer mailpitContainer = new MailpitContainer(DockerImageName.parse("axllent/mailpit:latest"));

    private EmailService createEmailService() {
        var sender = new JavaMailSenderImpl();
        sender.setHost(mailpitContainer.getHost());
        sender.setPort(mailpitContainer.getMappedPort(1025));
        var props = new Properties();
        props.setProperty("mail.smtp.auth", "false");
        sender.setJavaMailProperties(props);
        return new EmailService(sender);
    }

    @Test
    void sends_welcome_email() {
        var emailService = createEmailService();
        emailService.sendWelcomeEmail("test@example.com", "TestUser");

        awaitMailpit();

        var apiUrl = "http://" + mailpitContainer.getHost() + ":" + mailpitContainer.getMappedPort(8025) + "/api/v1/messages";
        var restTemplate = new org.springframework.web.client.RestTemplate();
        var response = restTemplate.getForEntity(apiUrl, String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Welcome to Art CMS");
        assertThat(response.getBody()).contains("test@example.com");
    }

    @Test
    void sends_new_content_notification() {
        var emailService = createEmailService();
        emailService.sendNewContentNotification("admin@example.com", "Image Content", "A beautiful sunset", 42L);

        awaitMailpit();

        var apiUrl = "http://" + mailpitContainer.getHost() + ":" + mailpitContainer.getMappedPort(8025) + "/api/v1/messages";
        var restTemplate = new org.springframework.web.client.RestTemplate();
        var response = restTemplate.getForEntity(apiUrl, String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("New Content");
        assertThat(response.getBody()).contains("admin@example.com");
        assertThat(response.getBody()).contains("Image Content");
    }

    @Test
    void does_not_throw_on_send_failure() {
        var service = new EmailService(null);
        service.sendWelcomeEmail("nowhere@example.com", "Ghost");
    }

    private void awaitMailpit() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
