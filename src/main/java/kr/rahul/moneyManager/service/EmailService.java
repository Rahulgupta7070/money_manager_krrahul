package kr.rahul.moneyManager.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {



    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${BREVO_FROM_MAIL}")
    private String fromEmail;

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            log.info("📤 Sending email via Brevo API to {}", to);

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", brevoApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = String.format("""
                {
                  "sender": {"email": "%s"},
                  "to": [{"email": "%s"}],
                  "subject": "%s",
                  "htmlContent": "%s"
                }
            """, fromEmail, to, subject, htmlContent.replace("\"", "'"));

            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Email sent successfully to {}", to);
            } else {
                log.error("❌ Failed to send email: {}", response.getBody());
                throw new RuntimeException("Email sending failed: " + response.getBody());
            }

        } catch (Exception e) {
            log.error("❌ Email sending failed: {}", e.getMessage());
            throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
        }
    }

    public void sendEmailWithExcelAttachment(String to,
                                             String subject,
                                             String htmlBody,
                                             byte[] excelData,
                                             String fileName) {
        try {
            log.info("📤 Sending email with Excel attachment to {}", to);

            // Encode Excel data to Base64 for API
            String base64Excel = Base64.getEncoder().encodeToString(excelData);

            // Prepare JSON body
            Map<String, Object> emailBody = new HashMap<>();
            emailBody.put("sender", Map.of("email", fromEmail));
            emailBody.put("to", List.of(Map.of("email", to)));
            emailBody.put("subject", subject);
            emailBody.put("htmlContent", htmlBody);

            // Add attachment
            emailBody.put("attachment", List.of(Map.of(
                    "content", base64Excel,
                    "name", fileName
            )));

            // Convert to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(emailBody);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            // Send request
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response =
                    restTemplate.postForEntity(BREVO_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Email sent successfully to {}", to);
            } else {
                log.error("❌ Brevo API error: {}", response.getBody());
                throw new RuntimeException("Failed to send email: " + response.getBody());
            }

        } catch (Exception e) {
            log.error("❌ Error sending email with attachment: {}", e.getMessage(), e);
            throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
        }
    }
}