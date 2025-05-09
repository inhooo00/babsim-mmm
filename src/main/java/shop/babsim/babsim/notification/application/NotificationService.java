package shop.babsim.babsim.notification.application;

import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.notification.api.dto.response.NotificationsResDto;
import shop.babsim.babsim.notification.domain.Notification;
import shop.babsim.babsim.notification.domain.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final SseEmitterManager sseEmitterManager;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    public SseEmitter connect(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        return sseEmitterManager.connect(member.getId());
    }

    @Transactional
    public void send(String email, Long targetMemberId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(MemberNotFoundException::new);

        String message = "name: " + member.getName() + "님이 알림을 보냈습니다.";

        notificationRepository.save(Notification.builder()
                .receiver(targetMember)
                .message(message)
                .build());

        sseEmitterManager.send(targetMember, message);
    }

    @Transactional
    public void send(Member targetMember, String message) {
        notificationRepository.save(Notification.builder()
                .receiver(targetMember)
                .message(message)
                .build());

        sseEmitterManager.send(targetMember, message);
    }

    public NotificationsResDto getNotifications(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        List<Notification> allByReceiver = notificationRepository.findAllByReceiver(member);

        return NotificationsResDto.from(allByReceiver);
    }

    @Transactional
    public void markAllNotificationsAsRead(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        notificationRepository.markAllAsRead(member.getId());
    }
}
