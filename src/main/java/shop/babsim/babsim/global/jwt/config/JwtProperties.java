package shop.babsim.babsim.global.jwt.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private Expire expire;

    @Getter
    @Setter
    public static class Expire {
        private Long access;
        private Long refresh;
    }
}
