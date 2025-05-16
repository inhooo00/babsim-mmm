package shop.babsim.babsim.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.babsim.babsim.global.entity.BaseEntity;
import shop.babsim.babsim.global.entity.Status;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.place.domain.Place;

@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseEntity {

    private int rating; // 내가 준 평점

    private String content;

    @Column(columnDefinition = "TEXT")
    private String feedImage;

    private int likes; // 공감 수

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @Builder
    public Review(int rating, String content, String feedImage, int likes, Member member, Place place) {
        this.rating = rating;
        this.content = content;
        this.feedImage = feedImage;
        this.likes = likes;
        this.status = Status.ACTIVE;
        this.member = member;
        this.place = place;
    }

    public void updateReview(int rating, String content, String feedImage) {
        this.rating = rating;
        this.content = content;
        this.feedImage = feedImage;
    }

    public void increasingLikes() {
        this.likes += 1;
    }

    public void decreasingLikes() {
        this.likes -= 1;
    }
}
