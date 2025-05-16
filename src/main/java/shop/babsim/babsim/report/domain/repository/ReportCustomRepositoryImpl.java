package shop.babsim.babsim.report.domain.repository;

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
import shop.babsim.babsim.report.api.dto.response.ReportResDto;
import shop.babsim.babsim.report.domain.QReport;
import shop.babsim.babsim.review.api.dto.response.ReviewInfoResDto;
import shop.babsim.babsim.review.domain.QReview;
import shop.babsim.babsim.review.domain.repository.ReviewCustomRepository;
import shop.babsim.babsim.review.s3.util.S3Util;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportCustomRepositoryImpl implements ReportCustomRepository {

    private final JPAQueryFactory queryFactory;
    private static final QReport report = QReport.report;

    @Override
    public Page<ReportResDto> findAllByMemberId(Long memberId, Pageable pageable) {
        BooleanBuilder condition = new BooleanBuilder();
        condition.and(report.status.eq(Status.ACTIVE));

        if (memberId != null) {
            condition.and(report.member.id.eq(memberId));
        }

        List<ReportResDto> content = queryFactory
                .select(Projections.constructor(
                        ReportResDto.class,
                        report.member.id,
                        report.businessName,
                        report.address,
                        report.menu,
                        report.price
                ))
                .from(report)
                .where(condition)
                .orderBy(report.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ReportResDto
                > parsedContent = content.stream()
                .map(reportResDto -> ReportResDto.builder()
                        .memberId(reportResDto.memberId())
                        .businessName(reportResDto.businessName())
                        .address(reportResDto.address())
                        .menu(reportResDto.menu())
                        .price(reportResDto.price())
                        .build()
                )
                .toList();

        long total = queryFactory
                .select(report.count())
                .from(report)
                .where(condition)
                .fetchOne();

        return PageableExecutionUtils.getPage(parsedContent, pageable, () -> total);
    }
}