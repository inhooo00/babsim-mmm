package shop.babsim.babsim.global.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import shop.babsim.babsim.auth.api.dto.request.IdTokenAndRefreshTokenDto;
import shop.babsim.babsim.auth.api.dto.response.IdTokenResDto;
import shop.babsim.babsim.auth.api.dto.response.UserInfo;
import shop.babsim.babsim.global.oauth.GoogleAuthService;
import shop.babsim.babsim.global.oauth.config.GoogleOAuthProperties;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GoogleAuthServiceTest {

    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate;
    @Mock private GoogleOAuthProperties googleOAuthProperties;

    @InjectMocks
    private GoogleAuthService googleAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(googleOAuthProperties.getClientId()).thenReturn("사용 안 함");
        when(googleOAuthProperties.getClientSecret()).thenReturn("사용 안 함");
        when(googleOAuthProperties.getRedirectUri()).thenReturn("사용 안 함");
    }

    @Test
    @DisplayName("구글 ID 토큰 응답 정상 파싱")
    void getIdToken_success() throws Exception {
        // given
        String code = "test-auth-code";
        String idToken = "header.payload.signature";
        String responseBody = "{\"id_token\": \"" + idToken + "\"}";

        ResponseEntity<String> mockResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(mockResponse);

        JsonNode jsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(responseBody)).thenReturn(jsonNode);
        when(jsonNode.get("id_token")).thenReturn(mock(JsonNode.class));

        // when
        IdTokenAndRefreshTokenDto result = googleAuthService.getToken(code);

        // then
        assertThat(result).isNotNull();
        verify(restTemplate).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    @DisplayName("ID 토큰에서 사용자 정보 추출")
    void getUserInfo_success() throws Exception {
        // given
        String payload = Base64.getUrlEncoder().encodeToString("{\"email\":\"test@kakao.com\"}".getBytes());
        String idToken = "header." + payload + ".signature";

        UserInfo mockUser = new UserInfo("test@gmail.com", "tester", "nickname", "profileImage");
        when(objectMapper.readValue(anyString(), eq(UserInfo.class))).thenReturn(mockUser);

        // when
        UserInfo result = googleAuthService.getUserInfo(idToken);

        // then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("test@gmail.com");
    }
}
