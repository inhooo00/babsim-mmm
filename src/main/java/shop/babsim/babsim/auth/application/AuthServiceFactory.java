package shop.babsim.babsim.auth.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.babsim.babsim.global.oauth.exception.OAuthException;
import shop.babsim.babsim.member.domain.SocialType;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;

@Service
public class AuthServiceFactory {

    private final Map<String, AuthService> authServiceMap;
    private final MemberRepository memberRepository;

    @Autowired
    public AuthServiceFactory(List<AuthService> authServices, MemberRepository memberRepository) {
        authServiceMap = new HashMap<>();
        for (AuthService authService : authServices) {
            authServiceMap.put(authService.getProvider(), authService);
        }
        this.memberRepository = memberRepository;
    }

    public AuthService getAuthService(String provider) {
        return authServiceMap.get(provider);
    }

    public AuthService getAuthServiceByEmail(String email) {
        SocialType socialType = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new)
                .getSocialType();

        AuthService authService = authServiceMap.get(socialType.name().toLowerCase());
        if (authService == null) {
            throw new OAuthException("지원하지 않는 소셜 로그인 유형입니다: " + socialType.name());
        }
        return authService;
    }
}
