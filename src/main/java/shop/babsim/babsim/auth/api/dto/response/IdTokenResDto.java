package shop.babsim.babsim.auth.api.dto.response;

import com.fasterxml.jackson.databind.JsonNode;

public record IdTokenResDto(
        JsonNode idToken
) {
}
