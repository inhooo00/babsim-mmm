package shop.babsim.babsim.notification.domain.repository;

public interface NotificationCustomRepository {
    void markAllAsRead(Long memberId);
}
