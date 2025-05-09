package shop.babsim.babsim.heart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import shop.babsim.babsim.heart.application.HeartService;
import shop.babsim.babsim.heart.domain.repository.HeartRepository;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.notification.application.NotificationService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class HeartServiceTest {

    @Mock
    private HeartRepository heartRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private HeartService heartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("리뷰 좋아요 추가 성공")
    void toggleReviewHeart_add_success() {
        String email = "inho@example.com";
        Long reviewId = 1L;

        Member member = mock(Member.class);
        Member reviewer = mock(Member.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(memberRepository.findByReviewId(reviewId)).thenReturn(Optional.of(reviewer));

        when(heartRepository.existsByMemberAndReviewId(member, reviewId)).thenReturn(false);
        when(member.getName()).thenReturn("Test User");

        heartService.toggleReviewHeart(email, reviewId);

        verify(memberRepository).findByEmail(email);
        verify(memberRepository).findByReviewId(reviewId);
        verify(heartRepository).addReviewHeart(member, reviewId);
        verify(notificationService).send(reviewer, "Test User님이 리뷰에 좋아요를 눌렀습니다.");
    }

    @Test
    @DisplayName("리뷰 좋아요 삭제 성공")
    void toggleReviewHeart_remove_success() {
        String email = "inho@example.com";
        Long reviewId = 1L;

        Member member = mock(Member.class);
        Member reviewer = mock(Member.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(memberRepository.findByReviewId(reviewId)).thenReturn(Optional.of(reviewer));

        when(heartRepository.existsByMemberAndReviewId(member, reviewId)).thenReturn(true);

        heartService.toggleReviewHeart(email, reviewId);

        verify(memberRepository).findByEmail(email);
        verify(memberRepository).findByReviewId(reviewId);
        verify(heartRepository).removeReviewHeart(member, reviewId);
        verify(notificationService, never()).send(any(), anyString());
    }

    @Test
    @DisplayName("좋아요 처리 실패 - 회원 없음")
    void toggleReviewHeart_fail_memberNotFound() {
        String email = "inho@example.com";
        Long reviewId = 1L;

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> heartService.toggleReviewHeart(email, reviewId))
                .isInstanceOf(MemberNotFoundException.class);

        verify(memberRepository).findByEmail(email);
        verifyNoMoreInteractions(heartRepository);
    }

    @Test
    @DisplayName("좋아요 처리 실패 - 리뷰어 없음")
    void toggleReviewHeart_fail_reviewerNotFound() {
        String email = "inho@example.com";
        Long reviewId = 1L;
        Member member = mock(Member.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(memberRepository.findByReviewId(reviewId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> heartService.toggleReviewHeart(email, reviewId))
                .isInstanceOf(MemberNotFoundException.class);

        verify(memberRepository).findByEmail(email);
        verify(memberRepository).findByReviewId(reviewId);
        verifyNoMoreInteractions(heartRepository);
    }
}
