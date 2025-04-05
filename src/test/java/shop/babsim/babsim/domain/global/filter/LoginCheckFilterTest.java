package shop.babsim.babsim.domain.global.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shop.babsim.babsim.global.filter.LoginCheckFilter;
import shop.babsim.babsim.global.jwt.TokenProvider;

class LoginCheckFilterTest {

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private LoginCheckFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("인증이 필요한 URI + 유효한 토큰 → chain.doFilter() 호출됨")
    void validToken_shouldProceed() throws Exception {
        // given
        when(request.getRequestURI()).thenReturn("/api/secure/resource");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token");
        when(tokenProvider.validateToken("valid.token")).thenReturn(true);

        // when
        filter.doFilter(request, response, chain);

        // then
        verify(chain).doFilter(request, response); // chain 실행 확인
        verify(response, never()).sendError(anyInt(), anyString());
    }
}

