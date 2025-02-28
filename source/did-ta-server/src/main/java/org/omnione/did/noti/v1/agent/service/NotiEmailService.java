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
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.noti.v1.agent.dto.email.RequestSendEmailReqDto;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import jakarta.annotation.PostConstruct;
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
    private final JavaMailSender javaMailSender;

    /**
     * Configures the JavaMailSender to disable SSL verification.
     *
     * @WARNING: This method disables SSL certificate verification and is intended
     * for use in development or testing environments only. Disabling SSL
     * verification can expose the application to security risks, such as
     * man-in-the-middle (MITM) attacks. Do not use this method in production
     * environments.
     */
    @PostConstruct
    public void configureJavaMailSender() {
        if (!(javaMailSender instanceof JavaMailSenderImpl)) {
            return;
        }
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) this.javaMailSender;

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.ssl.trust", "*");
        props.put("mail.smtp.ssl.checkserveridentity", "false");

        // Disable SSL verification
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
            mailSender.getJavaMailProperties().put("mail.smtp.ssl.socketFactory", sc.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("Failed to configure SSL context", e);
            throw new OpenDidException(ErrorCode.MAIL_CONFIGURATION_FAILED);
        }
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
        Resource resource = resourceLoader.getResource("classpath:templates/" + templateName.toString().toLowerCase() + ".html");
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("An error occurred while reading email template", e);
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
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            mimeMessage.setFrom(new InternetAddress(requestSendEmailReqDto.getSenderAddress()));
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(requestSendEmailReqDto.getEmail().getRecipientAddress()));
            mimeMessage.setSubject(requestSendEmailReqDto.getEmail().getTitle());
            mimeMessage.setContent(emailContent, "text/html; charset=utf-8");
            mimeMessage.setSentDate(Date.from(Instant.now()));
        };

        Thread currentThread = Thread.currentThread();
        ClassLoader originalClassLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(InternetAddress.class.getClassLoader());
        try {
            javaMailSender.send(mimeMessagePreparator);
        } catch (MailException e) {
            log.error("An error occurred while sending email", e);
            throw new OpenDidException(ErrorCode.EMAIL_SEND_FAILED);
        } finally {
            currentThread.setContextClassLoader(originalClassLoader);
        }

        return true;
    }
}
