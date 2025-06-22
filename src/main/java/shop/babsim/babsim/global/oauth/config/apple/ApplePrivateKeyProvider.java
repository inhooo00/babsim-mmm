package shop.babsim.babsim.global.oauth.config.apple;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class ApplePrivateKeyProvider {

    private static final String KEY_PATH = "/apple/AuthKey.p8";

    public ECPrivateKey getPrivateKey() {
        try (InputStream inputStream = getClass().getResourceAsStream(KEY_PATH)) {
            assert inputStream != null;

            byte[] keyBytes = inputStream.readAllBytes();
            String privateKeyPem = new String(keyBytes)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(privateKeyPem);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalStateException("Apple .p8 개인키 로딩 실패", e);
        }
    }
}
