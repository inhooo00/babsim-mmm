package shop.babsim.babsim.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.notification.api.dto.response.NotificationsResDto;
import shop.babsim.babsim.notification.application.NotificationService;
import shop.babsim.babsim.notification.application.SseEmitterManager;
import shop.babsim.babsim.notification.domain.Notification;
import shop.babsim.babsim.notification.domain.repository.NotificationRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private SseEmitterManager sseEmitterManager;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("알림 연결 성공")
    void connect_success() {
        String email = "inho@naver.com";
        Member member = mock(Member.class);
        SseEmitter sseEmitter = mock(SseEmitter.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(member.getId()).thenReturn(1L);
        when(sseEmitterManager.connect(1L)).thenReturn(sseEmitter);

        SseEmitter result = notificationService.connect(email);

        assertThat(result).isEqualTo(sseEmitter);
        verify(memberRepository).findByEmail(email);
        verify(sseEmitterManager).connect(1L);
    }

    @Test
    @DisplayName("알림 연결 실패 - 회원 없음")
    void connect_fail_memberNotFound() {
        String email = "inho@naver.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.connect(email))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("알림 전송 성공")
    void send_success() {
        String email = "inho@naver.com";
        Long targetMemberId = 2L;

        Member sender = mock(Member.class);
        Member receiver = mock(Member.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(sender));
        when(memberRepository.findById(targetMemberId)).thenReturn(Optional.of(receiver));

        notificationService.send(email, targetMemberId);

        verify(memberRepository).findByEmail(email);
        verify(memberRepository).findById(targetMemberId);
        verify(notificationRepository).save(any(Notification.class));
        verify(sseEmitterManager).send(eq(receiver), anyString());
    }

    @Test
    @DisplayName("알림 전송 실패 - 회원 없음")
    void send_fail_memberNotFound() {
        String email = "inho@naver.com";
        Long targetMemberId = 2L;

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.send(email, targetMemberId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("알림 리스트 조회 성공")
    void getNotifications_success() {
        String email = "inho@naver.com";
        Member member = mock(Member.class);
        List<Notification> notifications = List.of(mock(Notification.class));

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(notificationRepository.findAllByReceiver(member)).thenReturn(notifications);

        NotificationsResDto result = notificationService.getNotifications(email);

        assertThat(result).isNotNull();
        verify(memberRepository).findByEmail(email);
        verify(notificationRepository).findAllByReceiver(member);
    }

    @Test
    @DisplayName("알림 조회 실패 - 회원 없음")
    void getNotifications_fail_memberNotFound() {
        String email = "inho@naver.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.getNotifications(email))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("모든 알림 읽음 처리 성공")
    void markAllNotificationsAsRead_success() {
        String email = "inho@naver.com";
        Member member = mock(Member.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(member.getId()).thenReturn(1L);

        notificationService.markAllNotificationsAsRead(email);

        verify(memberRepository).findByEmail(email);
        verify(notificationRepository).markAllAsRead(1L);
    }

    @Test
    @DisplayName("모든 알림 읽음 처리 실패 - 회원 없음")
    void markAllNotificationsAsRead_fail_memberNotFound() {
        String email = "inho@naver.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAllNotificationsAsRead(email))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
