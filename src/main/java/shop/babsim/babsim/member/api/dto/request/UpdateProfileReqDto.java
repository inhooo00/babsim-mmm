package shop.babsim.babsim.member.api.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileReqDto {
    private String picture;
    private String nickname;

    public UpdateProfileReqDto(String picture, String nickname) {
        this.picture = picture;
        this.nickname = nickname;
    }
}
