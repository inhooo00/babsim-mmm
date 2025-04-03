package shop.babsim.babsim.auth.api.dto.response;


import lombok.Builder;
import shop.babsim.babsim.member.domain.Member;

@Builder
public record MemberLoginResDto(
        Member findMember
) {
    public static MemberLoginResDto from(Member member) {
        return MemberLoginResDto.builder()
                .findMember(member)
                .build();
    }
}
