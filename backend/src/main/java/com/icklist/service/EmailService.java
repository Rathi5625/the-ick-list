package com.icklist.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final SesClient sesClient;
    private final String senderEmail;
    private final boolean sesEnabled;

    public EmailService(
            SesClient sesClient,
            @Value("${aws.ses.sender:noreply@theicklist.app}") String senderEmail,
            @Value("${aws.ses.enabled:false}") boolean sesEnabled
    ) {
        this.sesClient = sesClient;
        this.senderEmail = senderEmail;
        this.sesEnabled = sesEnabled;
    }

    public void sendWelcomeEmail(String recipientEmail) {
        if (!sesEnabled) {
            // TODO: Live SES will be activated when AWS account access is connected
            logger.info("[SES] (STUBBED) Would send welcome email to '{}' from '{}'. Subject: 'Welcome to The Ick List!'", recipientEmail, senderEmail);
            return;
        }

        try {
            logger.info("[SES] Sending live welcome email to '{}'...", recipientEmail);
            SendEmailRequest request = SendEmailRequest.builder()
                    .source(senderEmail)
                    .destination(Destination.builder().toAddresses(recipientEmail).build())
                    .message(Message.builder()
                            .subject(Content.builder().data("Welcome to The Ick List!").charset("UTF-8").build())
                            .body(Body.builder()
                                    .text(Content.builder()
                                            .data("Welcome to The Ick List! Your account has been created. Upload your transaction CSV and get ready to be roasted.")
                                            .charset("UTF-8")
                                            .build())
                                    .build())
                            .build())
                    .build();

            sesClient.sendEmail(request);
            logger.info("[SES] Welcome email sent successfully to '{}'", recipientEmail);
        } catch (Exception e) {
            logger.warn("[SES] Could not send welcome email via SES ({}: {}). Continuing signup without failing.", e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
