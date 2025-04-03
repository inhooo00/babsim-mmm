package shop.babsim.babsim.global.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.auth.api.dto.response.IdTokenResDto;
import shop.babsim.babsim.auth.api.dto.response.UserInfo;
import shop.babsim.babsim.auth.application.AuthService;
import shop.babsim.babsim.global.oauth.exception.OAuthException;
import shop.babsim.babsim.member.domain.SocialType;

@Service
@Transactional(readOnly = true)
public class AppleAuthService implements AuthService {
    private static final String JWT_DELIMITER = "\\.";

    private final ObjectMapper objectMapper;

    public AppleAuthService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getProvider() {
        return String.valueOf(SocialType.APPLE).toLowerCase();
    }

    @Override
    public IdTokenResDto getIdToken(String code) {
        return null;
    }

    @Transactional
    @Override
    public UserInfo getUserInfo(String idToken) {
        String decodePayload = getDecodePayload(idToken);

        try {
            return objectMapper.readValue(decodePayload, UserInfo.class);
        } catch (JsonProcessingException e) {
            throw new OAuthException("id 토큰을 읽을 수 없습니다.");
        }
    }

    private String getDecodePayload(String idToken) {
        String payload = getPayload(idToken);

        return new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
    }

    private String getPayload(String idToken) {
        return idToken.split(JWT_DELIMITER)[1];
    }
}
