// GoogleAuthService.java
package shop.babsim.babsim.global.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import shop.babsim.babsim.auth.api.dto.request.IdTokenAndRefreshTokenDto;
import shop.babsim.babsim.auth.api.dto.response.IdTokenResDto;
import shop.babsim.babsim.auth.api.dto.response.UserInfo;
import shop.babsim.babsim.auth.application.AuthService;
import shop.babsim.babsim.global.oauth.config.GoogleOAuthProperties;
import shop.babsim.babsim.global.oauth.exception.OAuthException;
import shop.babsim.babsim.member.application.MemberService;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.SocialType;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleAuthService implements AuthService {

    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String JWT_DELIMITER = "\\.";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GoogleOAuthProperties googleOAuthProperties;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Override
    public IdTokenAndRefreshTokenDto getToken(String code) {
        Map<String, String> params = Map.of(
                "code", code,
                "scope", "https://www.googleapis.com/auth/userinfo.profile " +
                        "https://www.googleapis.com/auth/userinfo.email",
                "client_id", googleOAuthProperties.getClientId(),
                "client_secret", googleOAuthProperties.getClientSecret(),
                "redirect_uri", googleOAuthProperties.getRedirectUri(),
                "grant_type", "authorization_code"
        );

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_URL, params, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());

                String idToken = jsonNode.has("id_token") ? jsonNode.get("id_token").asText() : null;
                String refreshToken = jsonNode.has("refresh_token") ? jsonNode.get("refresh_token").asText() : null;

                return new IdTokenAndRefreshTokenDto(idToken, refreshToken);

            } catch (Exception e) {
                throw new OAuthException("Google ID 토큰을 파싱하는 데 실패했습니다.");
            }
        }

        throw new OAuthException("Google 토큰을 가져오지 못했습니다.");
    }


    @Override
    public String getProvider() {
        return SocialType.GOOGLE.name().toLowerCase();
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

    private IdTokenResDto parseGoogleIdToken(ResponseEntity<String> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                return new IdTokenResDto(jsonNode.get("id_token"));
            } catch (Exception e) {
                throw new OAuthException("ID 토큰을 파싱하는데 실패했습니다.");
            }
        }
        throw new OAuthException("구글 엑세스 토큰을 가져오는데 실패했습니다.");
    }

    private String getDecodePayload(String idToken) {
        String payload = getPayload(idToken);
        return new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
    }

    private String getPayload(String idToken) {
        return idToken.split(JWT_DELIMITER)[1];
    }

    @Override
    @Transactional
    public void unlink(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        String refreshToken = member.getProviderRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new OAuthException("Google refresh_token이 존재하지 않아 연결 해제를 진행할 수 없습니다.");
        }

        String accessToken = refreshAccessToken(refreshToken);

        revokeToken(accessToken);

        memberService.deleteMember(email);
    }

    private String refreshAccessToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleOAuthProperties.getClientId());
        params.add("client_secret", googleOAuthProperties.getClientSecret());
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new OAuthException("Google access_token 갱신 실패: " + response.getStatusCode());
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            if (jsonNode.has("access_token")) {
                return jsonNode.get("access_token").asText();
            } else {
                throw new OAuthException("access_token이 응답에 포함되지 않았습니다.");
            }
        } catch (Exception e) {
            throw new OAuthException("access_token 파싱 중 오류 발생");
        }
    }

    private void revokeToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("token", accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/revoke",
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new OAuthException("Google unlink 실패: " + response.getStatusCode());
        }
    }

}
