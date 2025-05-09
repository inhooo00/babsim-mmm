package shop.babsim.babsim.heart.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.heart.application.HeartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hearts")
public class HeartController implements HeartDocs {

    private final HeartService heartService;

    @PostMapping("/reviews/{reviewId}")
    public RspTemplate<Void> createOrDeleteReviewHeart(
            @CurrentUserEmail String email,
            @PathVariable Long reviewId) {

        heartService.toggleReviewHeart(email, reviewId);

        return new RspTemplate<>(HttpStatus.OK, "좋아요 등록/삭제 성공");
    }
}
