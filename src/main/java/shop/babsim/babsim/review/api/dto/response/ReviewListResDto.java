package shop.babsim.babsim.review.api.dto.response;

import java.util.List;
import lombok.Builder;
import shop.babsim.babsim.global.dto.PageInfoResDto;


@Builder
public record ReviewListResDto(
        List<ReviewInfoResDto> reviewListResDto,
        PageInfoResDto pageInfoResDto
) {
    public static ReviewListResDto of(List<ReviewInfoResDto> reviewListResDtos, PageInfoResDto pageInfoResDto) {
        return ReviewListResDto.builder()
                .reviewListResDto(reviewListResDtos)
                .pageInfoResDto(pageInfoResDto)
                .build();
    }
}