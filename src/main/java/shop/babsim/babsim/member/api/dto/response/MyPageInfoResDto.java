package shop.babsim.babsim.member.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.member.domain.Member;

@Builder
public record MyPageInfoResDto(
        String picture,
        String nickName,
        Integer reviewCount,
        Double ratingAverage,
        Integer reportCount
) {
    public static MyPageInfoResDto of(Member member, Integer reviewCount,
                                      Double ratingAverage, Integer reportCount) {
        return MyPageInfoResDto.builder()
                .picture(member.getPicture())
                .nickName(member.getNickname())
                .reviewCount(reviewCount)
                .ratingAverage(ratingAverage)
                .reportCount(reportCount)
                .build();
    }
}
