package shop.babsim.babsim.review.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.review.domain.Review;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Builder
public record ReviewSaveInfoResDto(
        List<String> feedImageUrls,
        int rating,
        String content,
        int likes,
        Long memberId
) {
    public static ReviewSaveInfoResDto of(Review review, Long memberId) {
        return ReviewSaveInfoResDto.builder()
                .feedImageUrls(parseCommaSeparatedList(review.getFeedImage()))
                .rating(review.getRating())
                .content(review.getContent())
                .likes(review.getLikes())
                .memberId(memberId)
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