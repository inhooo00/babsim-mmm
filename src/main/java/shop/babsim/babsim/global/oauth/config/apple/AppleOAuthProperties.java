package shop.babsim.babsim.global.oauth.config.apple;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth.apple")
public class AppleOAuthProperties {
    //yml에서 읽어옴
    private String clientId;
    private String teamId;
    private String keyId;
    private String redirectUri;
}
