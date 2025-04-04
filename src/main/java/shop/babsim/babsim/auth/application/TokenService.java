package shop.babsim.babsim.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.auth.api.dto.request.RefreshTokenReqDto;
import shop.babsim.babsim.auth.api.dto.response.MemberLoginResDto;
import shop.babsim.babsim.auth.exception.InvalidTokenException;
import shop.babsim.babsim.auth.exception.TokenNotFoundException;
import shop.babsim.babsim.global.jwt.TokenProvider;
import shop.babsim.babsim.global.jwt.api.dto.TokenDto;
import shop.babsim.babsim.global.jwt.domain.Token;
import shop.babsim.babsim.global.jwt.domain.repository.TokenRepository;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TokenDto getToken(MemberLoginResDto memberLoginResDto) {
        TokenDto tokenDto = tokenProvider.generateToken(memberLoginResDto.findMember().getEmail());

        tokenSaveAndUpdate(memberLoginResDto, tokenDto);

        return tokenDto;
    }

    private void tokenSaveAndUpdate(MemberLoginResDto memberLoginResDto, TokenDto tokenDto) {
        if (!tokenRepository.existsByMember(memberLoginResDto.findMember())) {
            tokenRepository.save(Token.builder()
                    .member(memberLoginResDto.findMember())
                    .refreshToken(tokenDto.refreshToken())
                    .build());
        }

        refreshTokenUpdate(memberLoginResDto, tokenDto);
    }

    private void refreshTokenUpdate(MemberLoginResDto memberLoginResDto, TokenDto tokenDto) {
        Token token = tokenRepository.findByMember(memberLoginResDto.findMember())
                .orElseThrow(TokenNotFoundException::new);
        token.refreshTokenUpdate(tokenDto.refreshToken());
    }

    @Transactional
    public TokenDto generateAccessToken(RefreshTokenReqDto refreshTokenReqDto) {
        if (isInvalidRefreshToken(refreshTokenReqDto.refreshToken())) {
            throw new InvalidTokenException();
        }

        Token token = tokenRepository.findByRefreshToken(refreshTokenReqDto.refreshToken())
                .orElseThrow(TokenNotFoundException::new);
        Member member = memberRepository.findById(token.getMember().getId())
                .orElseThrow(MemberNotFoundException::new);

        return tokenProvider.generateAccessTokenByRefreshToken(member.getEmail(), token.getRefreshToken());
    }

    private boolean isInvalidRefreshToken(String refreshToken) {
        return !tokenRepository.existsByRefreshToken(refreshToken) || !tokenProvider.validateToken(refreshToken);
    }
}
