package shop.babsim.babsim.heart.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.heart.domain.Heart;
import shop.babsim.babsim.heart.domain.QHeart;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.review.domain.Review;
import shop.babsim.babsim.review.exception.ReviewNotFoundException;


@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HeartCustomRepositoryImpl implements HeartCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    private static final QHeart heart = QHeart.heart;

    @Override
    @Transactional
    public void addReviewHeart(Member member, Long reviewId) {
        Review targetReview = entityManager.find(Review.class, reviewId);
        if (targetReview == null) {
            throw new ReviewNotFoundException();
        }

        Heart newHeart = new Heart(member, targetReview);
        entityManager.persist(newHeart);

        targetReview.increasingLikes();
    }

    @Override
    @Transactional
    public void removeReviewHeart(Member member, Long reviewId) {
        Review targetReview = entityManager.find(Review.class, reviewId);
        if (targetReview == null) {
            throw new ReviewNotFoundException();
        }

        long deletedCount = queryFactory
                .delete(heart)
                .where(heart.member.eq(member).and(heart.review.id.eq(reviewId)))
                .execute();

        if (deletedCount > 0) {
            targetReview.decreasingLikes();
        }
    }

    @Override
    public boolean existsByMemberAndReviewId(Member member, Long reviewId) {
        return queryFactory
                .selectOne()
                .from(heart)
                .where(heart.member.eq(member).and(heart.review.id.eq(reviewId)))
                .fetchFirst() != null;
    }
}
