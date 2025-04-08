package shop.babsim.babsim.heart.domain.repository;

import java.util.List;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.review.domain.Review;

public interface HeartCustomRepository {
    void createOrDeleteReviewHeart(Member member, Long reviewId);

    boolean existsByMemberAndReviewId(Member member, Long reviewId);

    List<Boolean> findHeartsForReviews(List<Review> cakes, Member member);
}
