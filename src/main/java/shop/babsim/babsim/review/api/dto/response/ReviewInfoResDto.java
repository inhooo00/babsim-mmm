package shop.babsim.babsim.review.api.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import shop.babsim.babsim.review.domain.Review;

@Builder
public record ReviewInfoResDto(
        String feedImage,
        int rating,
        String content,
        int likes,
        Long memberId,
        Long reviewId,
        LocalDateTime createdAt,
        String memberName,
        String memberImage
) {
    public static ReviewInfoResDto of(Review review, String feedImage) {

        return ReviewInfoResDto.builder()
                .feedImage(feedImage)
                .rating(review.getRating())
                .content(review.getContent())
                .likes(review.getLikes())
                .memberId(review.getMember().getId())
                .reviewId(review.getId())
                .createdAt(review.getCreatedAt())
                .memberName(review.getMember().getName())
                .memberImage(review.getMember().getPicture())
                .build();
    }
}