package shop.babsim.babsim.member.domain.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import shop.babsim.babsim.member.api.dto.response.MyPageInfoResDto;
import shop.babsim.babsim.member.domain.Grade;
import shop.babsim.babsim.member.domain.QMember;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.report.domain.QReport;
import shop.babsim.babsim.review.domain.QReview;

@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public MyPageInfoResDto findProfileByEmail(String email) {
        QMember member = QMember.member;
        QReview review = QReview.review;
        QReport report = QReport.report;

        // 1. 기본 정보 조회
        Tuple memberInfo = queryFactory
                .select(member.picture, member.nickname)
                .from(member)
                .where(member.email.eq(email))
                .fetchOne();

        if (memberInfo == null) throw new MemberNotFoundException();

        // 2. 리뷰 수, 평균 평점 조회
        Tuple reviewInfo = queryFactory
                .select(review.id.count(), review.rating.avg())
                .from(review)
                .where(review.member.email.eq(email))
                .fetchOne();

        int reviewCount = reviewInfo != null ? reviewInfo.get(review.id.count()).intValue() : 0;
        double ratingAverage = reviewInfo != null && reviewInfo.get(review.rating.avg()) != null ?
                reviewInfo.get(review.rating.avg()) : 0.0;

        // 3. 등급 계산
        Grade grade = Grade.getGradeByReviewCount(reviewCount);

        // 4. 제보 수 조회
        Long reportCount = queryFactory
                .select(report.id.count())
                .from(report)
                .where(report.member.email.eq(email))
                .fetchOne();

        return MyPageInfoResDto.builder()
                .picture(memberInfo.get(member.picture))
                .nickName(memberInfo.get(member.nickname))
                .reviewCount(reviewCount)
                .ratingAverage(ratingAverage)
                .reportCount(reportCount != null ? reportCount.intValue() : 0)
                .grade(grade.getName())
                .build();
    }
}
