package shop.babsim.babsim.member.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.member.domain.Grade;
import shop.babsim.babsim.member.domain.Member;

@Builder
public record MemberPageInfoResDto(
        String picture,
        String nickName,
        Integer reviewCount,
        Double ratingAverage,
        Integer reportCount,
        String grade,
        String reviewToNextGrade,
        Double progressRate
) {
    public static MemberPageInfoResDto of(Member member, Integer reviewCount,
                                          Double ratingAverage, Integer reportCount) {
        Grade currentGrade = Grade.getGradeByReviewCount(reviewCount);
        Grade nextGrade = currentGrade.getNext();

        int toNext = nextGrade != null ? nextGrade.getMinReviewCount() - reviewCount : 0;
        int base = currentGrade.getMinReviewCount();
        int range = (nextGrade != null ? nextGrade.getMinReviewCount() : base) - base;

        double rawProgress = (range > 0) ? ((double)(reviewCount - base) / range) : 1.0;
        double progressRate = Math.min(1.0, Math.round(rawProgress * 10) / 10.0); // 0.1 단위 반올림

        return MemberPageInfoResDto.builder()
                .picture(member.getPicture())
                .nickName(member.getNickname())
                .reviewCount(reviewCount)
                .ratingAverage(ratingAverage)
                .reportCount(reportCount)
                .grade(currentGrade.getName())
                .reviewToNextGrade(
                        nextGrade != null
                                ? "리뷰 " + toNext + "개 더 작성하면 " + nextGrade.getName() + " 단계예요!"
                                : "최고 등급입니다 🎉"
                )
                .progressRate(progressRate)
                .build();
    }
}
