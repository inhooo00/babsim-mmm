package shop.babsim.babsim.domain.global.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shop.babsim.babsim.auth.api.dto.request.TokenReqDto;
import shop.babsim.babsim.global.jwt.TokenProvider;
import shop.babsim.babsim.global.jwt.api.dto.TokenDto;
import shop.babsim.babsim.global.jwt.config.JwtProperties;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TokenProviderTest {

    @Mock // 가짜 객체 생성
    private JwtProperties jwtProperties;

    @Mock
    private JwtProperties.Expire expire;

    @InjectMocks // 테스트하고 싶은 객체 생성
    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        // Mockito 초기화
        MockitoAnnotations.openMocks(this);

        // 환경 변수 설정
        when(jwtProperties.getSecret()).thenReturn(
                Base64.getEncoder().encodeToString("this-is-a-very-secure-and-random-secret-key-for-jwt-signing-2025!!!".getBytes()));
        when(jwtProperties.getExpire()).thenReturn(expire);
        when(expire.getAccess()).thenReturn(1000L * 60 * 10);
        when(expire.getRefresh()).thenReturn(1000L * 60 * 60);

        // TokenProvider 초기화
        tokenProvider.init();
    }

    @Test
    @DisplayName("Access/Refresh 토큰 정상 생성 및 파싱 확인")
    void generateAndParseToken() {
        // given
        String email = "test@babsim.com";

        // when
        TokenDto tokenDto = tokenProvider.generateToken(email);
        String accessToken = tokenDto.accessToken();
        TokenReqDto tokenReqDto = new TokenReqDto(accessToken);
        String parsedEmail = tokenProvider.getUserEmailFromToken(tokenReqDto);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(tokenDto.refreshToken()).isNotNull();
        assertThat(parsedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("정상적인 토큰은 유효성 검사에 통과해야 함")
    void validateValidToken() {
        // given
        String email = "test@babsim.com";
        TokenDto tokenDto = tokenProvider.generateToken(email);

        // when
        boolean isValid = tokenProvider.validateToken(tokenDto.accessToken());

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("잘못된 형식의 토큰은 유효성 검사에서 실패해야 함")
    void invalidateMalformedToken() {
        // given
        String fakeToken = "not.a.valid.jwt";

        // when
        boolean isValid = tokenProvider.validateToken(fakeToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("AccessToken만 새로 발급하는 메서드 동작 확인")
    void generateAccessTokenByRefreshToken() {
        // given
        String email = "test@babsim.com";
        String oldRefreshToken = tokenProvider.generateToken(email).refreshToken();

        // when
        TokenDto result = tokenProvider.generateAccessTokenByRefreshToken(email, oldRefreshToken);

        // then
        assertThat(result.accessToken()).isNotNull();
        assertThat(result.refreshToken()).isEqualTo(oldRefreshToken); // refresh 그대로
    }
}
