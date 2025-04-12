package shop.babsim.babsim.report.discord.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record DiscordWebhookMessage(
        String content
) {
    public static DiscordWebhookMessage of(String content, Long memberId) {
        String timestamp = getCurrentTime(); // 현재 시간 가져오기
        String formattedMessage = formatMessage(content, memberId, timestamp);

        if (formattedMessage.length() >= 2000) {
            formattedMessage = formattedMessage.substring(0, 1993) + "\n...```";
        }
        return new DiscordWebhookMessage(formattedMessage);
    }

    private static String formatMessage(String content, Long memberId, String timestamp) {
        return """
                **[시스템 알림]**
                📢 메시지: `%s`
                👤 멤버 ID: `%s`
                🕒 시간: `%s`
                """.formatted(content, memberId, timestamp);
    }

    private static String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
