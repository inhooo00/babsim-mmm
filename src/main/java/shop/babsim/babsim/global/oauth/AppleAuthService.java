package shop.babsim.babsim.global.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
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
import shop.babsim.babsim.auth.api.dto.response.UserInfo;
import shop.babsim.babsim.auth.application.AuthService;
import shop.babsim.babsim.auth.domain.ApplePreSignup;
import shop.babsim.babsim.auth.domain.repository.ApplePreSignupRepository;
import shop.babsim.babsim.global.oauth.config.apple.AppleClientSecretGenerator;
import shop.babsim.babsim.global.oauth.config.apple.AppleOAuthProperties;
import shop.babsim.babsim.global.oauth.exception.OAuthException;
import shop.babsim.babsim.member.application.MemberService;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.SocialType;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppleAuthService implements AuthService {

    private static final String JWT_DELIMITER = "\\.";
    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";
    private static final String APPLE_REVOKE_URL = "https://appleid.apple.com/auth/revoke";

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final AppleOAuthProperties appleOAuthProperties;
    private final AppleClientSecretGenerator appleClientSecretGenerator;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ApplePreSignupRepository applePreSignupRepository;

    @Override
    public String getProvider() {
        return String.valueOf(SocialType.APPLE).toLowerCase();
    }

    @Override
    public IdTokenAndRefreshTokenDto getToken(String code) {
        String clientSecret = appleClientSecretGenerator.generate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", appleOAuthProperties.getClientId());
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", appleOAuthProperties.getRedirectUri());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                APPLE_TOKEN_URL,
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new OAuthException("Apple 토큰 발급 실패: " + response.getStatusCode());
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            String idToken = jsonNode.has("id_token") ? jsonNode.get("id_token").asText() : null;
            String refreshToken = jsonNode.has("refresh_token") ? jsonNode.get("refresh_token").asText() : null;

            return new IdTokenAndRefreshTokenDto(idToken, refreshToken);

        } catch (JsonProcessingException e) {
            throw new OAuthException("Apple 응답 파싱 실패");
        }
    }

    @Transactional
    @Override
    public void unlink(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        String refreshToken = member.getProviderRefreshToken();
        if (refreshToken == null) {
            throw new OAuthException("Apple refresh_token이 존재하지 않습니다.");
        }

        String clientSecret = appleClientSecretGenerator.generate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleOAuthProperties.getClientId());
        params.add("client_secret", clientSecret);
        params.add("token", refreshToken);
        params.add("token_type_hint", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                APPLE_REVOKE_URL,
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new OAuthException("Apple 사용자 연결 해제 실패: " + response.getStatusCode());
        }

        memberService.deleteMember(email);
    }

    @Transactional
    @Override
    public UserInfo getUserInfo(String idToken) {
        String decodePayload = getDecodePayload(idToken);

        try {
            JsonNode json = objectMapper.readTree(decodePayload);

            String sub = json.get("sub").asText();
            String email = json.has("email") ? json.get("email").asText() : null;
            String name = json.has("name") ? json.get("name").asText() : null;

            if (email != null) {
                if (!applePreSignupRepository.existsBySub(sub)) {
                    applePreSignupRepository.save(ApplePreSignup.of(sub, email, name));
                }
            } else {
                email = applePreSignupRepository.findBySub(sub)
                        .map(ApplePreSignup::getEmail)
                        .orElseThrow(() -> new OAuthException("Apple email 정보가 없습니다."));
            }

            return new UserInfo(email, name, null, name);

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
