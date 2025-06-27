/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.noti.v1.agent.service;

import org.omnione.did.base.datamodel.enums.EmailTemplateType;
import org.omnione.did.base.db.constant.NotificationServerType;
import org.omnione.did.base.db.constant.NotificationTemplateType;
import org.omnione.did.base.db.domain.NotificationTemplate;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.noti.v1.admin.dto.EmailConfigurationDto;
import org.omnione.did.noti.v1.admin.dto.SendTestEmailReqDto;
import org.omnione.did.noti.v1.agent.dto.email.RequestSendEmailReqDto;
import org.omnione.did.noti.v1.common.service.query.NotificationServerQueryService;
import org.omnione.did.noti.v1.common.service.query.NotificationTemplateQueryService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Service class for sending emails.
 * This service handles the preparation and sending of emails, including the use of predefined HTML templates.
 * It provides methods to generate email content based on template placeholders and to configure the mail sender.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotiEmailService {
    private final ResourceLoader resourceLoader;
    private final NotificationServerQueryService notificationServerQueryService;
    private final NotificationTemplateQueryService notificationTemplateQueryService;

    /**
     * Creates and configures a JavaMailSender instance dynamically.
     * This method retrieves the email configuration from the database
     * and applies the settings to the mail sender.
     * If no configuration is found, email sending will be disabled.
     *
     * @return Configured JavaMailSender instance, or null if no configuration is available.
     */
    private JavaMailSender createMailSender() {
        EmailConfigurationDto emailConfiguration = notificationServerQueryService.findEmailConfigurationOrNull();
        if (emailConfiguration == null) {
            log.warn("Mail configuration not found in database. Email sending is disabled.");
            return null;
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailConfiguration.getHost());
        mailSender.setPort(emailConfiguration.getPort());
        mailSender.setUsername(emailConfiguration.getUsername());
        mailSender.setPassword(emailConfiguration.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(emailConfiguration.getStartTlsEnabled()));
        props.put("mail.smtp.ssl.enable", String.valueOf(emailConfiguration.getSslEnabled()));
        props.put("mail.smtp.connectiontimeout", String.valueOf(emailConfiguration.getConnectionTimeout() * 1000));
        props.put("mail.smtp.timeout", String.valueOf(emailConfiguration.getReadTimeout() * 1000));
        props.put("mail.smtp.writetimeout", String.valueOf(emailConfiguration.getWriteTimeout() * 1000));

        if (emailConfiguration.getIgnoreSslValidation()) {
            log.warn("SSL certificate verification has been disabled.");
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.ssl.checkserveridentity", "false");

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                props.put("mail.smtp.ssl.socketFactory", sc.getSocketFactory());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                log.error("Failed to configure SSL context", e);
                throw new OpenDidException(ErrorCode.MAIL_CONFIGURATION_FAILED);
            }
        }

        return mailSender;
    }

    /**
     * Sends an email to the specified recipient.
     * This method processes the request, reads the appropriate email template,
     * replaces placeholders with actual data, and sends the email.
     *
     * @param requestSendEmailReqDto The request DTO containing the email information.
     * @return An {@link EmptyResDto} indicating that the email send operation was completed.
     * @throws OpenDidException If an error occurs while sending the email.
     */
    public EmptyResDto requestSendEmail(RequestSendEmailReqDto requestSendEmailReqDto) {
        try {
            log.debug("=== Starting requestSendEmail ===");

            // Generate email template
            log.debug("\t--> Retrieving Email Template");
            String emailTemplate = readEmailTemplate(requestSendEmailReqDto.getEmail().getTemplateType());

            // Generate email content
            log.debug("\t--> Generating Email Content");
            String emailContent = generateEmailContent(emailTemplate, requestSendEmailReqDto.getEmail().getContentData());

            // Send email
            log.debug("\t--> Sending Email");
            sendEmail(requestSendEmailReqDto, emailContent);

            log.debug("*** Finished requestSendEmail ***");

            return new EmptyResDto();
        } catch (OpenDidException e) {
            log.error("An error occurred while requesting send email", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while requesting send email", e);
            throw new OpenDidException(ErrorCode.FAILED_API_SEND_EMAIL);
        }
    }

    /**
     * Reads the specified email template from the classpath.
     * The template is an HTML file stored under the "templates" directory.
     *
     * @param templateName The name of the email template to read.
     * @return The contents of the email template as a String.
     * @throws OpenDidException If an error occurs while reading the email template.
     */
    private String readEmailTemplate(EmailTemplateType templateName) {
        if (templateName == EmailTemplateType.ISSUE_VC) {
            NotificationTemplate notificationTemplate = notificationTemplateQueryService.findNotificationTemplate(NotificationServerType.EMAIL, NotificationTemplateType.ISSUE_VC);
            return notificationTemplate.getTemplate();
        } else if (templateName == EmailTemplateType.RESTORE_DID) {
            NotificationTemplate notificationTemplate = notificationTemplateQueryService.findNotificationTemplate(NotificationServerType.EMAIL, NotificationTemplateType.RESTORE_DID);
            return notificationTemplate.getTemplate();
        } else {
            throw new OpenDidException(ErrorCode.EMAIL_TEMPLATE_READ_FAILED);
        }
    }

    /**
     * Generates the email content by replacing placeholders in the email template with the specified content data.
     * Placeholders in the template are denoted by curly braces (e.g., {username}).
     *
     * @param emailTemplate The email template to use.
     * @param contentData The data to use for replacing placeholders in the email template.
     * @return The email content with placeholders replaced by the specified data.
     */
    private String generateEmailContent(String emailTemplate, Map<String, String> contentData) {
        Pattern pattern = Pattern.compile("\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(emailTemplate);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = contentData.getOrDefault(key, "");
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Sends an email to the specified recipient.
     * This method prepares and sends the email using the given content and recipient details.
     *
     * @param requestSendEmailReqDto The request DTO containing the email information.
     * @param emailContent The content of the email to send.
     * @return True if the email was sent successfully.
     * @throws OpenDidException If an error occurs while sending the email.
     */
    private boolean sendEmail(RequestSendEmailReqDto requestSendEmailReqDto, String emailContent) {
        EmailConfigurationDto emailConfigurationDto = notificationServerQueryService.findEmailConfiguration();
        String sender = requestSendEmailReqDto.getSenderAddress() != null ? requestSendEmailReqDto.getSenderAddress() : emailConfigurationDto.getSender();

        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            mimeMessage.setFrom(new InternetAddress(sender));
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(requestSendEmailReqDto.getEmail().getRecipientAddress()));
            mimeMessage.setSubject(requestSendEmailReqDto.getEmail().getTitle());
            mimeMessage.setContent(emailContent, "text/html; charset=utf-8");
            mimeMessage.setSentDate(Date.from(Instant.now()));
        };

        Thread currentThread = Thread.currentThread();
        ClassLoader originalClassLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(InternetAddress.class.getClassLoader());
        try {
            JavaMailSender javaMailSender = createMailSender();
            if (javaMailSender == null) {
                log.error("JavaMailSender is null");
                throw new OpenDidException(ErrorCode.EMAIL_SEND_FAILED);
            }

            javaMailSender.send(mimeMessagePreparator);
        } catch (MailException e) {
            log.error("An error occurred while sending email", e);
            throw new OpenDidException(ErrorCode.EMAIL_SEND_FAILED);
        } finally {
            currentThread.setContextClassLoader(originalClassLoader);
        }

        return true;
    }

    /**
     * Sends a test email to the specified recipient.
     * The email contains a simple test message.
     *
     * @param recipientEmail The email address of the recipient.
     * @throws OpenDidException If an error occurs while sending the email.
     */
    public void sendTestEmail(String recipientEmail) {
        JavaMailSender mailSender = createMailSender();
        if (mailSender == null) {
            throw new OpenDidException(ErrorCode.MAIL_CONFIGURATION_FAILED);
        }

        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            mimeMessage.setFrom(new InternetAddress(notificationServerQueryService.findEmailConfigurationOrNull().getSender()));
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            mimeMessage.setSubject("OpenDID Test Email");
            mimeMessage.setText("This email was sent from the OpenDID TA Admin to test the email configuration.");
            mimeMessage.setSentDate(Date.from(Instant.now()));
        };

        try {
            mailSender.send(mimeMessagePreparator);
            log.info("Test email successfully sent to {}", recipientEmail);
        } catch (MailException e) {
            log.error("An error occurred while sending the test email", e);
            throw new OpenDidException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private JavaMailSender createMailSender(SendTestEmailReqDto sendTestEmailReqDto) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(sendTestEmailReqDto.getHost());
        mailSender.setPort(sendTestEmailReqDto.getPort());
        mailSender.setUsername(sendTestEmailReqDto.getUsername());
        mailSender.setPassword(sendTestEmailReqDto.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(sendTestEmailReqDto.getStartTlsEnabled()));
        props.put("mail.smtp.ssl.enable", String.valueOf(sendTestEmailReqDto.getSslEnabled()));
        props.put("mail.smtp.connectiontimeout", String.valueOf(sendTestEmailReqDto.getConnectionTimeout() * 1000));
        props.put("mail.smtp.timeout", String.valueOf(sendTestEmailReqDto.getReadTimeout() * 1000));
        props.put("mail.smtp.writetimeout", String.valueOf(sendTestEmailReqDto.getWriteTimeout() * 1000));

        if (sendTestEmailReqDto.getIgnoreSslValidation()) {
            log.warn("SSL certificate verification has been disabled.");
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.ssl.checkserveridentity", "false");

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                props.put("mail.smtp.ssl.socketFactory", sc.getSocketFactory());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                log.error("Failed to configure SSL context", e);
                throw new OpenDidException(ErrorCode.MAIL_CONFIGURATION_FAILED);
            }
        }

        return mailSender;
    }

    public void sendTestEmail(SendTestEmailReqDto sendTestEmailReqDto) {
        JavaMailSender mailSender = createMailSender(sendTestEmailReqDto);
        if (mailSender == null) {
            throw new OpenDidException(ErrorCode.MAIL_CONFIGURATION_FAILED);
        }

        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            mimeMessage.setFrom(new InternetAddress(sendTestEmailReqDto.getSender()));
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(sendTestEmailReqDto.getRecipient()));
            mimeMessage.setSubject("OpenDID Test Email");
            mimeMessage.setText("This email was sent from the OpenDID TA Admin to test the email configuration.");
            mimeMessage.setSentDate(Date.from(Instant.now()));
        };

        try {
            mailSender.send(mimeMessagePreparator);
            log.info("Test email successfully sent to {}", sendTestEmailReqDto.getRecipient());
        } catch (MailException e) {
            log.error("An error occurred while sending the test email", e);
            throw new OpenDidException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

}
