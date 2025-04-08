package shop.babsim.babsim.heart.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import shop.babsim.babsim.bookmark.api.dto.response.BookmarkResListDto;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;

@Tag(name = "[리뷰 좋아요 API]", description = "리뷰 좋아요 관련 API")
public interface HeartDocs {

    @Operation(summary = "리뷰 좋아요 등록 / 삭제", description = "리뷰 좋아요 등록 / 삭제를 진행합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "리뷰 좋아요 등록 / 삭제 성공",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<Void> createOrDeleteReviewHeart(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "reviewId", required = true) Long reviewId);
}