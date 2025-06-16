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
import shop.babsim.babsim.auth.api.dto.request.IdTokenAndRefreshTokenDto;
import shop.babsim.babsim.auth.api.dto.response.IdTokenResDto;
import shop.babsim.babsim.auth.api.dto.response.UserInfo;
import shop.babsim.babsim.auth.application.AuthService;
import shop.babsim.babsim.global.oauth.config.KakaoOAuthProperties;
import shop.babsim.babsim.global.oauth.exception.OAuthException;
import shop.babsim.babsim.member.application.MemberService;
import shop.babsim.babsim.member.domain.SocialType;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoAuthService implements AuthService {

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String JWT_DELIMITER = "\\.";
    private static final String KAKAO_UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final KakaoOAuthProperties kakaoOAuthProperties;
    private final MemberService memberService;

    @Override
    public IdTokenAndRefreshTokenDto getToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOAuthProperties.getRestApiKey());
        params.add("redirect_uri", kakaoOAuthProperties.getRedirectUrl());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_TOKEN_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                String idToken = jsonNode.has("id_token") ? jsonNode.get("id_token").asText() : null;
                String refreshToken = jsonNode.has("refresh_token") ? jsonNode.get("refresh_token").asText() : null;

                return new IdTokenAndRefreshTokenDto(idToken, refreshToken);

            } else {
                throw new OAuthException("카카오 토큰 요청 실패: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new OAuthException("카카오 토큰 요청 중 예외 발생");
        }
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

    @Override
    @Transactional
    public void unlink(String email, String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        String accessToken = refreshAccessToken(refreshToken);
        unlinkWithAccessToken("Bearer " + accessToken);
        memberService.deleteMember(email);
    }

    private String refreshAccessToken(String refreshToken) {
        log.info("[refreshAccessToken] 호출됨, refresh_token = {}", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", kakaoOAuthProperties.getRestApiKey());
        params.add("refresh_token", refreshToken);

        log.info("[refreshAccessToken] 요청 params = {}", params);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_TOKEN_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            log.info("[refreshAccessToken] 응답 = {}", response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                if (jsonNode.has("access_token")) {
                    return jsonNode.get("access_token").asText();
                } else {
                    throw new OAuthException("응답에 access_token이 포함되지 않았습니다.");
                }

            } else {
                throw new OAuthException("카카오 access_token 갱신 실패: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("[카카오 토큰 갱신] 예외 발생: {}", e.getMessage(), e);
            throw new OAuthException("카카오 access_token 갱신 중 예외 발생: " + e.getMessage());
        }
    }

    private void unlinkWithAccessToken(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_UNLINK_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("카카오 회원 해제 실패: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("카카오 unlink 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
