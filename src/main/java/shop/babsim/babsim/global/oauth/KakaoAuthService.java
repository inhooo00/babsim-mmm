package shop.babsim.babsim.global.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import shop.babsim.babsim.auth.api.dto.response.IdTokenResDto;
import shop.babsim.babsim.auth.api.dto.response.UserInfo;
import shop.babsim.babsim.auth.application.AuthService;
import shop.babsim.babsim.global.oauth.config.KakaoOAuthProperties;
import shop.babsim.babsim.global.oauth.exception.OAuthException;
import shop.babsim.babsim.member.domain.SocialType;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoAuthService implements AuthService {

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String JWT_DELIMITER = "\\.";

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final KakaoOAuthProperties kakaoOAuthProperties;

    @Override
    public IdTokenResDto getIdToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOAuthProperties.getRestApiKey());
        params.add("redirect_uri", kakaoOAuthProperties.getRedirectUrl());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_TOKEN_URL,
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return new IdTokenResDto(jsonNode.get("id_token"));
            } catch (Exception e) {
                throw new RuntimeException("ID 토큰을 파싱하는데 실패했습니다.", e);
            }
        }

        throw new OAuthException("카카오 액세스 토큰을 가져오는데 실패했습니다.");
    }

    @Override
    public String getProvider() {
        return SocialType.KAKAO.name().toLowerCase();
    }

    @Transactional
    @Override
    public UserInfo getUserInfo(String idToken) {
        String payload = getPayload(idToken);
        String decodePayload = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);

        try {
            return objectMapper.readValue(decodePayload, UserInfo.class);
        } catch (JsonProcessingException e) {
            throw new OAuthException("id 토큰을 읽을 수 없습니다.");
        }
    }

    private String getPayload(String idToken) {
        return idToken.split(JWT_DELIMITER)[1];
    }
}
