package shop.babsim.babsim.report.discord.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record DiscordWebhookMessage(@JsonProperty("content") String content) {

    public static DiscordWebhookMessage of(String reportContent, Long memberId) {
        String timestamp = getCurrentTime();

        String formattedMessage = formatMessage(reportContent, memberId, timestamp);

        if (formattedMessage.length() >= 2000) {
            formattedMessage = formattedMessage.substring(0, 1993) + "\n...```";
        }

        return new DiscordWebhookMessage(formattedMessage);
    }

    private static String formatMessage(String reportContent, Long memberId, String timestamp) {
        return """
                **[신고 알림]**
                👤 사용자: `%s`
                🕒 시간: `%s`
                📝 내용:
                ```
                %s
                ```
                """.formatted(memberId != null ? memberId : "익명", timestamp, reportContent);
    }

    private static String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
