package shop.babsim.babsim.global.oauth.config.apple;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AppleClientSecretGenerator {

    private final AppleOAuthProperties appleOAuthProperties;
    private final ApplePrivateKeyProvider privateKeyProvider;

    public String generate() {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(60 * 60 * 6);

        return Jwts.builder()
                .setHeaderParam("kid", appleOAuthProperties.getKeyId())
                .setIssuer(appleOAuthProperties.getTeamId())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .setAudience("https://appleid.apple.com")
                .setSubject(appleOAuthProperties.getClientId())
                .setId(UUID.randomUUID().toString())
                .signWith(privateKeyProvider.getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }
}
