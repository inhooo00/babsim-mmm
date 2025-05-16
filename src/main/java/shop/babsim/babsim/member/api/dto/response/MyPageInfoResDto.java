package shop.babsim.babsim.member.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.member.domain.Grade;
import shop.babsim.babsim.member.domain.Member;

@Builder
public record MyPageInfoResDto(
        String picture,
        String nickName,
        Integer reviewCount,
        Double ratingAverage,
        Integer reportCount,
        String grade
) {
    public static MyPageInfoResDto of(Member member, Integer reviewCount,
                                      Double ratingAverage, Integer reportCount) {
        Grade grade = Grade.getGradeByReviewCount(reviewCount);
        return MyPageInfoResDto.builder()
                .picture(member.getPicture())
                .nickName(member.getNickname())
                .reviewCount(reviewCount)
                .ratingAverage(ratingAverage)
                .reportCount(reportCount)
                .grade(grade.getName())
                .build();
    }
}
