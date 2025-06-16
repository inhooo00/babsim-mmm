package shop.babsim.babsim.auth.api.dto.request;

public record IdTokenAndRefreshTokenDto(
        String idToken,
        String refreshToken
) {

}
