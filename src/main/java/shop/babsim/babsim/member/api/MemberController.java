package shop.babsim.babsim.member.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.member.api.dto.response.MyPageInfoResDto;
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
}
