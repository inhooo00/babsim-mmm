package shop.babsim.babsim.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.babsim.babsim.auth.api.dto.request.TokenReqDto;
import shop.babsim.babsim.global.jwt.api.dto.TokenDto;
import shop.babsim.babsim.global.jwt.config.JwtProperties;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUserEmailFromToken(TokenReqDto tokenReqDto) {
        return extractClaims(tokenReqDto.authCode()).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public TokenDto generateToken(String email) {
        return TokenDto.builder()
                .accessToken(generateAccessToken(email))
                .refreshToken(generateRefreshToken())
                .build();
    }

    public TokenDto generateAccessTokenByRefreshToken(String email, String refreshToken) {
        return TokenDto.builder()
                .accessToken(generateAccessToken(email))
                .refreshToken(refreshToken)
                .build();
    }

    public String generateAccessToken(String email) {
        return generateTokenWithSubject(email, jwtProperties.getExpire().getAccess());
    }

    public String generateRefreshToken() {
        return generateTokenWithSubject(null, jwtProperties.getExpire().getRefresh());
    }

    private String generateTokenWithSubject(String subject, long expirationMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS512);

        if (subject != null) {
            builder.setSubject(subject);
        }

        return builder.compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
