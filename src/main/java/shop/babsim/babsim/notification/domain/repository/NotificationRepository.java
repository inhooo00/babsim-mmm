package shop.babsim.babsim.notification.domain.repository;

import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.notification.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationCustomRepository {

    List<Notification> findAllByReceiver(Member receiver);
    boolean existsByReceiverEmailAndIsReadFalse(String email);
}
