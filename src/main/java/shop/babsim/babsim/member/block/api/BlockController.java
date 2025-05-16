package shop.babsim.babsim.member.block.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.member.block.api.dto.request.BlockUserReqDto;
import shop.babsim.babsim.member.block.application.BlockService;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController implements BlockDocs{

    private final BlockService blockService;

    // 유저 차단
    @PostMapping
    public RspTemplate<Void> blockUser(
            @CurrentUserEmail String email,
            @RequestBody BlockUserReqDto blockUserReqDto) {
        blockService.blockUser(email, blockUserReqDto);
        return new RspTemplate<>(HttpStatus.OK, "유저를 차단했습니다.");
    }

    // 유저 차단 해제
    @DeleteMapping
    public RspTemplate<Void> unblockUser(
            @CurrentUserEmail String email,
            @RequestBody BlockUserReqDto blockUserReqDto) {
        blockService.unblockUser(email, blockUserReqDto);
        return new RspTemplate<>(HttpStatus.OK, "유저 차단을 해제했습니다.");
    }
}
