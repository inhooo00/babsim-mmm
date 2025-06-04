package shop.babsim.babsim.member.api.dto.response;

import java.util.List;

public record ProfileInfoResListDto(
        List<ProfileInfoResDto> profiles
) {
    public static ProfileInfoResListDto from(List<ProfileInfoResDto> profiles) {
        return new ProfileInfoResListDto(profiles);
    }
}
