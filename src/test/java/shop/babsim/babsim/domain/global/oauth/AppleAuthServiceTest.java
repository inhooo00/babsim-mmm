package shop.babsim.babsim.domain.global.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shop.babsim.babsim.auth.api.dto.response.UserInfo;
import shop.babsim.babsim.global.oauth.AppleAuthService;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AppleAuthServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AppleAuthService appleAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("ID 토큰으로 사용자 정보 추출")
    void getUserInfo_success() throws Exception {
        // given
        String payload = Base64.getUrlEncoder().encodeToString("{\"email\":\"test@apple.com\"}".getBytes());
        String idToken = "header." + payload + ".signature";

        UserInfo expectedUser = new UserInfo("test@apple.com", "testuser", "testnickname", "testprofile");
        when(objectMapper.readValue(anyString(), eq(UserInfo.class))).thenReturn(expectedUser);

        // when
        UserInfo result = appleAuthService.getUserInfo(idToken);

        // then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("test@apple.com");
    }
}
