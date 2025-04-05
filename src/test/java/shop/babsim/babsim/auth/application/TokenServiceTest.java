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

import shop.babsim.babsim.auth.api.dto.request.RefreshTokenReqDto;
import shop.babsim.babsim.auth.api.dto.response.MemberLoginResDto;
import shop.babsim.babsim.auth.exception.InvalidTokenException;
import shop.babsim.babsim.global.jwt.TokenProvider;
import shop.babsim.babsim.global.jwt.api.dto.TokenDto;
import shop.babsim.babsim.global.jwt.domain.Token;
import shop.babsim.babsim.global.jwt.domain.repository.TokenRepository;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;

class TokenServiceTest {
    // 하면 할수록 의존성이 너무 많아서 목킹하는 데 힘든데, 이게 최선일까..?
    // 뭐 단위 테스트니까 이정도가 최선일지도..
    @Mock TokenProvider tokenProvider;
    @Mock TokenRepository tokenRepository;
    @Mock MemberRepository memberRepository;

    @InjectMocks
    TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getToken 호출 시 TokenDto를 반환")
    void shouldReturnTokenSuccessfully() {
        // given
        Member member = mock(Member.class);
        when(member.getEmail()).thenReturn("test@babsim.com");

        MemberLoginResDto dto = mock(MemberLoginResDto.class);
        when(dto.findMember()).thenReturn(member);

        TokenDto tokenDto = new TokenDto("access-token", "refresh-token");
        when(tokenProvider.generateToken(anyString())).thenReturn(tokenDto);

        when(tokenRepository.existsByMember(member)).thenReturn(false);

        Token savedToken = mock(Token.class);
        when(tokenRepository.save(any(Token.class))).thenReturn(savedToken);
        when(tokenRepository.findByMember(member)).thenReturn(Optional.of(savedToken));

        // when
        TokenDto result = tokenService.getToken(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
    }



    @Test
    @DisplayName("유효한 refreshToken으로 accessToken 재발급 성공")
    void shouldGenerateAccessToken_withValidRefreshToken() {
        // given
        String refreshToken = "valid-refresh-token";
        RefreshTokenReqDto req = new RefreshTokenReqDto(refreshToken);

        Member member = Member.builder().email("test@babsim.com").build();
        Token token = Token.builder().member(member).refreshToken(refreshToken).build();

        when(tokenRepository.existsByRefreshToken(refreshToken)).thenReturn(true);
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(token));
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(tokenProvider.generateAccessTokenByRefreshToken(any(), any())).thenReturn(
                new TokenDto("new-access", refreshToken));

        // when
        TokenDto result = tokenService.generateAccessToken(req);

        // then
        assertThat(result.accessToken()).isEqualTo("new-access");
        assertThat(result.refreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("refreshToken이 유효하지 않으면 예외 발생")
    void shouldThrowException_whenInvalidRefreshToken() {
        String invalidToken = "bad-token";
        RefreshTokenReqDto req = new RefreshTokenReqDto(invalidToken);

        when(tokenRepository.existsByRefreshToken(invalidToken)).thenReturn(false);

        assertThatThrownBy(() -> tokenService.generateAccessToken(req))
                .isInstanceOf(InvalidTokenException.class);
    }
}
