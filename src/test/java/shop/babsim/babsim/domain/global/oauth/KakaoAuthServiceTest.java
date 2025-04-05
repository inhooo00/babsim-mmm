package shop.babsim.babsim.domain.global.oauth;

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
import shop.babsim.babsim.auth.api.dto.response.IdTokenResDto;
import shop.babsim.babsim.auth.api.dto.response.UserInfo;
import shop.babsim.babsim.global.oauth.KakaoAuthService;
import shop.babsim.babsim.global.oauth.config.KakaoOAuthProperties;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KakaoAuthServiceTest {

    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate;
    @Mock private KakaoOAuthProperties kakaoOAuthProperties;

    @InjectMocks
    private KakaoAuthService kakaoAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(kakaoOAuthProperties.getRestApiKey()).thenReturn("사용 안 함");
        when(kakaoOAuthProperties.getRedirectUrl()).thenReturn("사용 안 함");
    }

    @Test
    @DisplayName("카카오 ID 토큰 성공적으로 파싱")
    void getIdToken_success() throws Exception {
        // given
        String code = "auth-code";
        String idToken = "header.payload.signature";
        String mockResponseBody = "{\"id_token\": \"" + idToken + "\"}";

        ResponseEntity<String> mockResponse = new ResponseEntity<>(mockResponseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        JsonNode mockJson = mock(JsonNode.class);
        when(objectMapper.readTree(mockResponseBody)).thenReturn(mockJson);
        when(mockJson.get("id_token")).thenReturn(mock(JsonNode.class));

        // when
        IdTokenResDto result = kakaoAuthService.getIdToken(code);

        // then
        assertThat(result).isNotNull();
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("ID 토큰으로 사용자 정보 추출")
    void getUserInfo_success() throws Exception {
        // given
        String payload = Base64.getUrlEncoder().encodeToString("{\"email\":\"test@kakao.com\"}".getBytes());
        String idToken = "header." + payload + ".signature";

        UserInfo expectedUser = new UserInfo("test@kakao.com", "testuser", "testnickname", "testprofile");
        when(objectMapper.readValue(anyString(), eq(UserInfo.class))).thenReturn(expectedUser);

        // when
        UserInfo result = kakaoAuthService.getUserInfo(idToken);

        // then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("test@kakao.com");
    }
}
