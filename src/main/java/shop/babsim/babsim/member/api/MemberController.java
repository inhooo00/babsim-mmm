package shop.babsim.babsim.member.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.member.api.dto.request.UpdateProfileReqDto;
import shop.babsim.babsim.member.api.dto.response.MyPageInfoResDto;
import shop.babsim.babsim.member.api.dto.response.ProfileInfoResDto;
import shop.babsim.babsim.member.api.dto.response.ProfileInfoResListDto;
import shop.babsim.babsim.member.api.dto.response.UpdateMyPageInfoResDto;
import shop.babsim.babsim.member.application.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController implements MemberDocs{

    private final MemberService memberService;

    @GetMapping("/my-page")
    public RspTemplate<MyPageInfoResDto> myProfileInfo(@CurrentUserEmail String email) {
        MyPageInfoResDto memberResDto = memberService.findMyProfileByEmail(email);
        return new RspTemplate<>(HttpStatus.OK, "내 프로필 정보", memberResDto);
    }

    @GetMapping("/my-page/{memberId}")
    public RspTemplate<MyPageInfoResDto> memberProfileInfo(@PathVariable Long memberId) {
        MyPageInfoResDto memberResDto = memberService.findProfileByEmail(memberId);
        return new RspTemplate<>(HttpStatus.OK, "상대방 프로필 정보", memberResDto);
    }

    // 내 수정 정보 조회
    @GetMapping("/update-my-page")
    public RspTemplate<UpdateMyPageInfoResDto> findMyProfile(@CurrentUserEmail String email) {
        return new RspTemplate<>(HttpStatus.OK, "내 수정 정보 조회 성공", memberService.findMyProfile(email));
    }

    // 내 정보 수정
    @PatchMapping("/update-my-page")
    public RspTemplate<UpdateMyPageInfoResDto> updateMyProfile(
            @CurrentUserEmail String email,
            @RequestBody UpdateProfileReqDto updateProfileReqDto) {
        return new RspTemplate<>(HttpStatus.OK,
                "내 수정 정보 수정 성공",
                memberService.updateMyProfile(email, updateProfileReqDto));
    }

    @DeleteMapping()
    public RspTemplate<Void> deleteMyProfile(@CurrentUserEmail String email) {
        memberService.deleteMember(email);
        return new RspTemplate<>(HttpStatus.OK, "회원 탈퇴 성공");
    }

    @GetMapping("/profile-images")
    public RspTemplate<ProfileInfoResListDto> getProfileImages(@CurrentUserEmail String email) {
        ProfileInfoResListDto profiles = memberService.getAvailableProfiles(email);
        return new RspTemplate<>(HttpStatus.OK, "프로필 이미지 리스트 조회 성공", profiles);
    }
}
