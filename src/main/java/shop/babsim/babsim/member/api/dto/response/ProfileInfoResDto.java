package shop.babsim.babsim.member.api.dto.response;

import lombok.Builder;

@Builder
public record ProfileInfoResDto(
        int profileLevel,
        String name,
        String imageUrl,
        boolean isLocked
) {}
