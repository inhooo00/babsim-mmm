package shop.babsim.babsim.global.annotation;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import shop.babsim.babsim.auth.api.dto.request.TokenReqDto;
import shop.babsim.babsim.global.annotationresolver.CurrentUserEmailArgumentResolver;
import shop.babsim.babsim.global.jwt.TokenProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CurrentUserEmailArgumentResolverTest {

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private MethodParameter methodParameter;

    @InjectMocks
    private CurrentUserEmailArgumentResolver resolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Bearer 토큰이 있는 경우 이메일 추출 성공")
    void resolveArgument_validBearerToken() {
        // given
        String token = "Bearer test.jwt.token";
        String expectedEmail = "test@babsim.com";

        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);
        when(tokenProvider.getUserEmailFromToken(any(TokenReqDto.class))).thenReturn(expectedEmail);

        // when
        Object result = resolver.resolveArgument(methodParameter, null, webRequest, null);

        // then
        assertThat(result).isEqualTo(expectedEmail);
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 null 반환")
    void resolveArgument_noAuthHeader() {
        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

        Object result = resolver.resolveArgument(methodParameter, null, webRequest, null);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Authorization 헤더가 Bearer 형식이 아니면 null 반환")
    void resolveArgument_invalidBearerFormat() {
        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("InvalidToken");

        Object result = resolver.resolveArgument(methodParameter, null, webRequest, null);
        assertThat(result).isNull();
    }
}
