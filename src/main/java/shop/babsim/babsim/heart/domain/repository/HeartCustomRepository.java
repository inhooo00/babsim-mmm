package shop.babsim.babsim.heart.domain.repository;

import java.util.List;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.review.domain.Review;

public interface HeartCustomRepository {
    void addReviewHeart(Member member, Long reviewId);
    void removeReviewHeart(Member member, Long reviewId);
    boolean existsByMemberAndReviewId(Member member, Long reviewId);
}

