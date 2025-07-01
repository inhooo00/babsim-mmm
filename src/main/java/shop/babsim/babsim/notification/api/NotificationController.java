package shop.babsim.babsim.notification.api;

import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.notification.api.dto.response.NotificationsResDto;
import shop.babsim.babsim.notification.application.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController implements NotificationDocs {

    private final NotificationService notificationService;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(
            @CurrentUserEmail String email) {
        return ResponseEntity.ok(notificationService.connect(email));
    }

    @PostMapping("/send/{targetMemberId}")
    public RspTemplate<Void> send(@CurrentUserEmail String email,
                                  @PathVariable Long targetMemberId) {
        notificationService.send(email, targetMemberId);

        return new RspTemplate<>(HttpStatus.OK, "알림 전송 성공.");

    }

    @GetMapping("/notifications")
    public RspTemplate<NotificationsResDto> getNotifications(@CurrentUserEmail String email) {
        NotificationsResDto notifications = notificationService.getNotifications(email);

        notificationService.markAllNotificationsAsRead(email);

        return new RspTemplate<>(HttpStatus.OK, "알림 조회 성공.", notifications);
    }

}
