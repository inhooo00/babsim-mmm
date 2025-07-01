package shop.babsim.babsim.member.domain.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import shop.babsim.babsim.member.api.dto.response.MemberPageInfoResDto;
import shop.babsim.babsim.member.api.dto.response.MyPageInfoResDto;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.QMember;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.notification.domain.QNotification;
import shop.babsim.babsim.report.domain.QReport;
import shop.babsim.babsim.review.domain.QReview;

@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public MyPageInfoResDto findMyProfileByEmail(String email) {
        QMember member = QMember.member;
        QReview review = QReview.review;
        QReport report = QReport.report;
        QNotification notification = QNotification.notification;

        // 기본 정보
        Tuple memberInfo = queryFactory
                .select(member.picture, member.nickname)
                .from(member)
                .where(member.email.eq(email))
                .fetchOne();

        if (memberInfo == null) throw new MemberNotFoundException();

        // 리뷰 정보
        Tuple reviewInfo = queryFactory
                .select(review.id.count(), review.rating.avg())
                .from(review)
                .where(review.member.email.eq(email))
                .fetchOne();

        int reviewCount = reviewInfo != null ? reviewInfo.get(review.id.count()).intValue() : 0;
        double ratingAverage = reviewInfo != null && reviewInfo.get(review.rating.avg()) != null ?
                reviewInfo.get(review.rating.avg()) : 0.0;

        // 신고 수
        Long reportCount = queryFactory
                .select(report.id.count())
                .from(report)
                .where(report.member.email.eq(email))
                .fetchOne();

        // 회원 객체
        Member foundMember = queryFactory
                .selectFrom(member)
                .where(member.email.eq(email))
                .fetchOne();

        if (foundMember == null) throw new MemberNotFoundException();

        Boolean hasUnreadNotification = queryFactory
                .selectOne()
                .from(notification)
                .where(
                        notification.receiver.email.eq(email),
                        notification.isRead.eq(false)
                )
                .fetchFirst() != null;

        return MyPageInfoResDto.of(
                foundMember,
                reviewCount,
                ratingAverage,
                reportCount != null ? reportCount.intValue() : 0,
                hasUnreadNotification
        );
    }

    @Override
    public MemberPageInfoResDto findProfileByEmail(String email) {
        QMember member = QMember.member;
        QReview review = QReview.review;

        Tuple memberInfo = queryFactory
                .select(member.picture, member.nickname)
                .from(member)
                .where(member.email.eq(email))
                .fetchOne();

        if (memberInfo == null) throw new MemberNotFoundException();

        Tuple reviewInfo = queryFactory
                .select(review.id.count(), review.rating.avg())
                .from(review)
                .where(review.member.email.eq(email))
                .fetchOne();

        int reviewCount = reviewInfo != null ? reviewInfo.get(review.id.count()).intValue() : 0;
        double ratingAverage = reviewInfo != null && reviewInfo.get(review.rating.avg()) != null ?
                reviewInfo.get(review.rating.avg()) : 0.0;

        Long reportCount = queryFactory
                .select(QReport.report.id.count())
                .from(QReport.report)
                .where(QReport.report.member.email.eq(email))
                .fetchOne();

        Member foundMember = queryFactory
                .selectFrom(member)
                .where(member.email.eq(email))
                .fetchOne();

        if (foundMember == null) throw new MemberNotFoundException();

        return MemberPageInfoResDto.of(
                foundMember,
                reviewCount,
                ratingAverage,
                reportCount != null ? reportCount.intValue() : 0
        );
    }

    @Override
    public int getReviewCountByEmail(String email) {
        QReview review = QReview.review;

        Integer reviewCount = queryFactory
                .select(review.id.count().intValue())
                .from(review)
                .where(review.member.email.eq(email))
                .fetchOne();

        return reviewCount != null ? reviewCount : 0;
    }

}
