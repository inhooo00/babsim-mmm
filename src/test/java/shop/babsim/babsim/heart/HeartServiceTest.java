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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class HeartServiceTest {

    @Mock
    private HeartRepository heartRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ddHeartService heartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("좋아요 생성/삭제 성공")
    void createOrDeleteReviewHeart_success() {
        String email = "user@example.com";
        Long reviewId = 1L;
        Member member = mock(Member.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        heartService.createOrDeleteReviewHeart(email, reviewId);

        verify(memberRepository).findByEmail(email);
        verify(heartRepository).createOrDeleteReviewHeart(member, reviewId);
    }

    @Test
    @DisplayName("좋아요 생성/삭제 실패 - 회원 없음")
    void createOrDeleteReviewHeart_memberNotFound() {
        String email = "noone@example.com";
        Long reviewId = 1L;

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> heartService.createOrDeleteReviewHeart(email, reviewId))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
