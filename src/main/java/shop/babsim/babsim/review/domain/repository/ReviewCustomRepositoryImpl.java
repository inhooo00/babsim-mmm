package shop.babsim.babsim.review.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.global.entity.Status;
import shop.babsim.babsim.review.api.dto.response.ReviewInfoResDto;
import shop.babsim.babsim.review.domain.QReview;
import shop.babsim.babsim.review.domain.Review;
import shop.babsim.babsim.review.s3.util.S3Util;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final S3Util s3Util;
    private static final QReview review = QReview.review;

    @Override
    public Page<ReviewInfoResDto> findAllByPlaceIdExcludingBlocked(String placeId, List<Long> blockedIds, Pageable pageable) {
        BooleanBuilder condition = new BooleanBuilder();
        condition.and(review.status.eq(Status.ACTIVE));

        if (placeId != null) {
            condition.and(review.place.placeId.eq(placeId));
        }

        if (blockedIds != null && !blockedIds.isEmpty()) {
            condition.and(review.member.id.notIn(blockedIds));
        }

        List<ReviewInfoResDto> content = queryFactory
                .select(Projections.constructor(
                        ReviewInfoResDto.class,
                        review.feedImage,
                        review.rating,
                        review.content,
                        review.likes,
                        review.member.id,
                        review.id,
                        review.createdAt,
                        review.member.name,
                        review.member.picture
                ))
                .from(review)
                .where(condition)
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ReviewInfoResDto> parsedContent = content.stream()
                .map(feedInfoResDto -> ReviewInfoResDto.builder()
                        .feedImageUrls(s3Util.getFileUrl(feedInfoResDto.feedImageUrls()))
                        .rating(feedInfoResDto.rating())
                        .content(feedInfoResDto.content())
                        .likes(feedInfoResDto.likes())
                        .memberId(feedInfoResDto.memberId())
                        .reviewId(feedInfoResDto.reviewId())
                        .createdAt(feedInfoResDto.createdAt())
                        .memberName(feedInfoResDto.memberName())
                        .memberImage(feedInfoResDto.memberImage())
                        .build()
                )
                .toList();

        long total = queryFactory
                .select(review.count())
                .from(review)
                .where(condition)
                .fetchOne();

        return PageableExecutionUtils.getPage(parsedContent, pageable, () -> total);
    }

    @Override
    public Double getRatingAvgByPlaceId(String placeId) {
        Double averageRating = queryFactory
                .select(review.rating.avg())
                .from(review)
                .where(
                        review.place.placeId.eq(placeId)
                )
                .fetchOne();

        return averageRating != null ? averageRating : 0.0;
    }

    @Override
    public Page<ReviewInfoResDto> findAllByMemberId(Long memberId, Pageable pageable) {
        BooleanBuilder condition = new BooleanBuilder();
        condition.and(review.status.eq(Status.ACTIVE));

        if (memberId != null) {
            condition.and(review.member.id.eq(memberId));
        }

        List<Review> reviews = queryFactory
                .selectFrom(review)
                .where(condition)
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ReviewInfoResDto> parsedContent = reviews.stream()
                .map(r -> ReviewInfoResDto.of(r, r.getFeedImage()))
                .toList();

        long total = queryFactory
                .select(review.count())
                .from(review)
                .where(condition)
                .fetchOne();

        return PageableExecutionUtils.getPage(parsedContent, pageable, () -> total);
    }


}