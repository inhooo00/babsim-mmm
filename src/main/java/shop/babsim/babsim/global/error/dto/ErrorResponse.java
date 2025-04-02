package shop.babsim.babsim.global.error.dto;

public record ErrorResponse(
        int statusCode,
        String message
) {
}