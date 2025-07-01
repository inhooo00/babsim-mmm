package shop.babsim.babsim.notification.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.notification.domain.Notification;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Builder
public record NotificationsResDto(
        List<NotificationResDto> notifications
) {
    public static NotificationsResDto from(List<Notification> notifications) {
        return NotificationsResDto.builder()
                .notifications(notifications.stream()
                        .map(NotificationResDto::from)
                        .toList())
                .build();
    }

    @Builder
    private record NotificationResDto(
            String message,
            Boolean isRead,
            String timeAgo
    ) {
        public static NotificationResDto from(Notification notification) {
            return new NotificationResDto(
                    notification.getMessage(),
                    notification.getIsRead(),
                    formatTimeAgo(notification.getCreatedAt())
            );
        }

        private static String formatTimeAgo(LocalDateTime createdAt) {
            if (createdAt == null) return "";

            Duration duration = Duration.between(createdAt, LocalDateTime.now());

            if (duration.toMinutes() < 1) return "방금 전";
            if (duration.toMinutes() < 60) return duration.toMinutes() + "분 전";
            if (createdAt.toLocalDate().isEqual(LocalDate.now())) {
                return duration.toHours() + "시간 전";
            }
            long days = ChronoUnit.DAYS.between(createdAt.toLocalDate(), LocalDate.now());
            return days + "일 전";
        }
    }
}
