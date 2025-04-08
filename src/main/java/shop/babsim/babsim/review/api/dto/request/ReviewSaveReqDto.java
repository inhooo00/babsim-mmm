package shop.babsim.babsim.review.api.dto.request;

import java.util.List;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.review.domain.Review;

public record ReviewSaveReqDto(
        int rating,
        String content,
        String placeId
) {
    public Review toEntity(Member member, Place place, List<String> imageUrls) {
        return Review.builder()
                .rating(rating)
                .content(content)
                .feedImage(String.join(",", imageUrls))
                .likes(0)
                .member(member)
                .place(place)
                .build();
    }
}