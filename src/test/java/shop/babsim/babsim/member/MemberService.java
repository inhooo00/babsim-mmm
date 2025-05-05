package shop.babsim.babsim.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import shop.babsim.babsim.member.api.dto.response.MyPageInfoResDto;
import shop.babsim.babsim.member.application.MemberService;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("내 프로필 조회 성공")
    void findMyProfileByEmail_success() {
        String email = "inho@example.com";
        MyPageInfoResDto expectedDto = mock(MyPageInfoResDto.class);

        when(memberRepository.findProfileByEmail(email)).thenReturn(expectedDto);

        MyPageInfoResDto result = memberService.findMyProfileByEmail(email);

        assertThat(result).isEqualTo(expectedDto);
        verify(memberRepository).findProfileByEmail(email);
    }

    @Test
    @DisplayName("상대방 프로필 조회 성공")
    void findProfileByEmail_success() {
        Long memberId = 1L;
        String email = "inho@example.com";
        Member member = mock(Member.class);
        MyPageInfoResDto expectedDto = mock(MyPageInfoResDto.class);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(member.getEmail()).thenReturn(email);
        when(memberRepository.findProfileByEmail(email)).thenReturn(expectedDto);

        MyPageInfoResDto result = memberService.findProfileByEmail(memberId);

        assertThat(result).isEqualTo(expectedDto);
        verify(memberRepository).findById(memberId);
        verify(memberRepository).findProfileByEmail(email);
    }

    @Test
    @DisplayName("상대방 프로필 조회 실패 - 회원 없음")
    void findProfileByEmail_fail_memberNotFound() {
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.findProfileByEmail(memberId))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
