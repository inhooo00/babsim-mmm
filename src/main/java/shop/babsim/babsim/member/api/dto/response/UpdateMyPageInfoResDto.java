package shop.babsim.babsim.member.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.member.domain.Grade;
import shop.babsim.babsim.member.domain.Member;

@Builder
public record UpdateMyPageInfoResDto(
        String picture,
        String nickName
) {
    public static UpdateMyPageInfoResDto from(Member member) {
        return UpdateMyPageInfoResDto.builder()
                .picture(member.getPicture())
                .nickName(member.getNickname())
                .build();
    }
}
