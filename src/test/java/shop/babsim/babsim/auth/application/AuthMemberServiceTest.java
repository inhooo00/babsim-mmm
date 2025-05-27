package shop.babsim.babsim.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shop.babsim.babsim.auth.api.dto.response.MemberLoginResDto;
import shop.babsim.babsim.auth.api.dto.response.UserInfo;
import shop.babsim.babsim.auth.exception.EmailNotFoundException;
import shop.babsim.babsim.auth.exception.ExistsMemberEmailException;
import shop.babsim.babsim.global.entity.Status;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.Role;
import shop.babsim.babsim.member.domain.SocialType;
import shop.babsim.babsim.member.domain.repository.MemberRepository;

class AuthMemberServiceTest {

    @Mock private MemberRepository memberRepository;

    @InjectMocks private AuthMemberService authMemberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이메일이 null이면 예외 발생")
    void shouldThrowException_whenEmailIsNull() {
        UserInfo userInfo = new UserInfo(null, "인호", "인호사진", "인호");

        assertThatThrownBy(() -> authMemberService.saveUserInfo(userInfo, SocialType.GOOGLE))
                .isInstanceOf(EmailNotFoundException.class);
    }

    @Test
    @DisplayName("이메일이 존재하지 않는 경우 신규 유저 저장")
    void shouldSaveNewMember_whenEmailNotExists() {
        // given
        UserInfo userInfo = new UserInfo("new@babsim.com", "인호", "인호사진", "인호");
        when(memberRepository.findByEmail(userInfo.email())).thenReturn(Optional.empty());

        Member newMember = Member.builder()
                .email(userInfo.email())
                .name("인호")
                .nickname("인호")
                .picture("인호사진")
                .status(Status.ACTIVE)
                .role(Role.ROLE_USER)
                .socialType(SocialType.GOOGLE)
                .introduction("")
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(newMember);

        // when
        MemberLoginResDto result = authMemberService.saveUserInfo(userInfo, SocialType.GOOGLE);

        // then
        assertThat(result.findMember().getEmail()).isEqualTo(userInfo.email());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("이메일이 존재하고 소셜 타입도 일치하면 로그인 처리")
    void shouldLogin_whenEmailExistsAndSocialTypeMatches() {
        // given
        UserInfo userInfo = new UserInfo("exist@babsim.com", "인호", "인호사진", "인호");

        Member existingMember = Member.builder()
                .email(userInfo.email())
                .name("인호")
                .nickname("인호")
                .picture("인호사진")
                .status(Status.ACTIVE)
                .role(Role.ROLE_USER)
                .socialType(SocialType.GOOGLE)
                .introduction("")
                .build();

        when(memberRepository.findByEmail(userInfo.email())).thenReturn(Optional.of(existingMember));

        // when
        MemberLoginResDto result = authMemberService.saveUserInfo(userInfo, SocialType.GOOGLE);

        // then
        assertThat(result.findMember().getEmail()).isEqualTo(userInfo.email());
        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("다른 소셜 타입으로 로그인 시 예외 발생")
    void shouldThrowException_whenSocialTypeMismatch() {
        UserInfo userInfo = new UserInfo("exist@babsim.com", "인호", "인호사진", "url");
        Member existingMember = Member.builder()
                .email("exist@babsim.com")
                .socialType(SocialType.KAKAO)
                .picture("url")
                .build();

        when(memberRepository.findByEmail(userInfo.email())).thenReturn(Optional.of(existingMember));

        assertThatThrownBy(() -> authMemberService.saveUserInfo(userInfo, SocialType.GOOGLE))
                .isInstanceOf(ExistsMemberEmailException.class);
    }
}
