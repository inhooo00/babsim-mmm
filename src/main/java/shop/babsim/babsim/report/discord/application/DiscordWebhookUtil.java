package shop.babsim.babsim.report.discord.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import shop.babsim.babsim.report.discord.dto.DiscordWebhookMessage;

@Service
public class DiscordWebhookUtil {

    @Value("${logging.discord.webhook-url}")
    private String discordWebhookUrl;

    public void sendDiscordMessage(String message, Long memberId) {
        try {
            DiscordWebhookMessage discordMessage = DiscordWebhookMessage.of(message, memberId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<DiscordWebhookMessage> request = new HttpEntity<>(discordMessage, headers);

            new RestTemplate().postForEntity(discordWebhookUrl, request, String.class);

            System.out.println("✅ 디스코드 알림 전송 완료: " + message);
        } catch (Exception e) {
            System.err.println("❌ 디스코드 메시지 전송 실패: " + e.getMessage());
        }
    }
}