package shop.babsim.babsim.auth.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.babsim.babsim.auth.api.dto.request.IdTokenAndRefreshTokenDto;
import shop.babsim.babsim.auth.api.dto.request.RefreshTokenReqDto;
import shop.babsim.babsim.auth.api.dto.request.TokenReqDto;
import shop.babsim.babsim.auth.api.dto.response.MemberLoginResDto;
import shop.babsim.babsim.auth.api.dto.response.UserInfo;
import shop.babsim.babsim.auth.application.AuthMemberService;
import shop.babsim.babsim.auth.application.AuthService;
import shop.babsim.babsim.auth.application.AuthServiceFactory;
import shop.babsim.babsim.auth.application.TokenService;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.jwt.api.dto.TokenDto;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.member.domain.SocialType;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController implements AuthDocs {

    private final AuthServiceFactory authServiceFactory;
    private final AuthMemberService memberService;
    private final TokenService tokenService;

    @GetMapping("oauth2/callback/{provider}")
    public IdTokenAndRefreshTokenDto callback(@PathVariable(name = "provider") String provider,
                                              @RequestParam(name = "code") String code) {
        AuthService authService = authServiceFactory.getAuthService(provider);
        return authService.getToken(code);
    }

    @PostMapping("/{provider}/token")
    public RspTemplate<TokenDto> generateAccessAndRefreshToken(
            @PathVariable(name = "provider") String provider,
            @RequestBody TokenReqDto tokenReqDto) {
        AuthService authService = authServiceFactory.getAuthService(provider);
        UserInfo userInfo = authService.getUserInfo(tokenReqDto.authCode());

        MemberLoginResDto getMemberDto = memberService.saveUserInfo(userInfo,
                SocialType.valueOf(provider.toUpperCase()), tokenReqDto.providerRefreshToken());
        TokenDto getToken = tokenService.getToken(getMemberDto);

        return new RspTemplate<>(HttpStatus.OK, "토큰 발급", getToken);
    }

    @PostMapping("/token/access")
    public RspTemplate<TokenDto> generateAccessToken(@RequestBody RefreshTokenReqDto refreshTokenReqDto) {
        TokenDto getToken = tokenService.generateAccessToken(refreshTokenReqDto);

        return new RspTemplate<>(HttpStatus.OK, "액세스 토큰 발급", getToken);
    }

    @PostMapping("/unlink")
    public RspTemplate<String> unlinkSocial(@CurrentUserEmail String email) {
        AuthService authService = authServiceFactory.getAuthServiceByEmail(email);
        String provider = authService.getProvider();
        authService.unlink(email);
        return new RspTemplate<>(HttpStatus.OK, provider.toUpperCase() + " 계정 연결 해제", "연결 해제 성공");
    }
}
