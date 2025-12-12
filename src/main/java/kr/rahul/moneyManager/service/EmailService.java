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

            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", Map.of("email", fromEmail));
            payload.put("to", List.of(Map.of("email", to)));
            payload.put("subject", subject);
            payload.put("htmlContent", htmlContent);

            ObjectMapper mapper = new ObjectMapper();
            HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✔ Email sent successfully to {}", to);
            } else {
                log.error("❌ Brevo send email failure: {}", response.getBody());
                throw new RuntimeException("Email sending failed: " + response.getBody());
            }

        } catch (Exception e) {
            log.error("❌ Email sending error: {}", e.getMessage());
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

            String base64Excel = Base64.getEncoder().encodeToString(excelData);

            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", Map.of("email", fromEmail));
            payload.put("to", List.of(Map.of("email", to)));
            payload.put("subject", subject);
            payload.put("htmlContent", htmlBody);

            // correct Note: key is `"attachments"` plural
            payload.put("attachments", List.of(Map.of(
                    "content", base64Excel,
                    "name", fileName
            )));

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(payload);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(BREVO_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✔ Email with attachment sent successfully to {}", to);
            } else {
                log.error("❌ Attachment sending failed: {}", response.getBody());
                throw new RuntimeException("Failed to send mail: " + response.getBody());
            }

        } catch (Exception e) {
            log.error("❌ Mail attachment exception: {}", e.getMessage());
            throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
        }
    }
}
