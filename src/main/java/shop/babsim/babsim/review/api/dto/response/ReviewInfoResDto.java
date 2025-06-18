package shop.babsim.babsim.review.api.dto.response;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import shop.babsim.babsim.review.domain.Review;

@Builder
public record ReviewInfoResDto(
        List<String> feedImageUrls,
        double rating,
        String content,
        int likes,
        Long memberId,
        Long reviewId,
        LocalDateTime createdAt,
        String memberName,
        String memberImage,
        String businessName,
        String placeId
) {
    public static ReviewInfoResDto of(Review review, String feedImage) {

        return ReviewInfoResDto.builder()
                .feedImageUrls(parseCommaSeparatedList(review.getFeedImage()))
                .rating(review.getRating())
                .content(review.getContent())
                .likes(review.getLikes())
                .memberId(review.getMember().getId())
                .reviewId(review.getId())
                .createdAt(review.getCreatedAt())
                .memberName(review.getMember().getName())
                .memberImage(review.getMember().getPicture())
                .businessName(review.getPlace().getBusinessName())
                .placeId(review.getPlace().getPlaceId())
                .build();
    }

    private static List<String> parseCommaSeparatedList(String input) {
        if (input == null || input.isBlank()) return Collections.emptyList();

        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

}