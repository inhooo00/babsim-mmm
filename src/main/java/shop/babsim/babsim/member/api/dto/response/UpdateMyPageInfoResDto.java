package shop.babsim.babsim.member.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.member.domain.Grade;
import shop.babsim.babsim.member.domain.Member;

@Builder
public record UpdateMyPageInfoResDto(
        String picture,
        String nickName,
        String email,
        String grade
) {
    public static UpdateMyPageInfoResDto of(Member member, Integer reviewCount) {
        Grade grade = Grade.getGradeByReviewCount(reviewCount);
        return UpdateMyPageInfoResDto.builder()
                .picture(member.getPicture())
                .nickName(member.getNickname())
                .email(member.getEmail())
                .grade(grade.getName())
                .build();
    }
}
