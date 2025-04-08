package shop.babsim.babsim.review.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.review.domain.Review;

@Builder
public record ReviewSaveInfoResDto(
        String feedImage,
        int rating,
        String content,
        int likes,
        Long memberId
) {
    public static ReviewSaveInfoResDto of(Review review, Long memberId) {
        return ReviewSaveInfoResDto.builder()
                .feedImage(review.getFeedImage())
                .rating(review.getRating())
                .content(review.getContent())
                .likes(review.getLikes())
                .memberId(memberId)
                .build();
    }
}