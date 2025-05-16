package shop.babsim.babsim.complaint.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import shop.babsim.babsim.complaint.api.dto.request.ReviewReportReqDto;
import shop.babsim.babsim.complaint.domain.ComplaintStatus;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.member.api.dto.response.MyPageInfoResDto;

@Tag(name = "[리뷰 신고 API]", description = "리뷰 신고 관련 API")
public interface ComplaintDocs {

    @Operation(summary = "리뷰 신고", description = "리뷰를 신고합니다. type 값 : INAPPROPRIATE_CONTENT, SPAM, ABUSE, OTHER",
            responses = {
                    @ApiResponse(responseCode = "200", description = "리뷰 신고 성공",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<Void> reportReview(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "reviewId ,  "
                    + "   INAPPROPRIATE_CONTENT,   // 부적절한 내용\n"
                    + "    SPAM,                    // 스팸 또는 광고\n"
                    + "    ABUSE,                   // 욕설 또는 악의적 리뷰\n"
                    + "    OTHER                    // 기타 사유"
                    + " 신고 이유를 dto로 작성", required = true) ReviewReportReqDto reviewReportReqDto);


    @Operation(summary = "리뷰 신고 처리 상태 변경", description = "리뷰 신고 처리 상태를 변경합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "변경 성공",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<Void> updateComplaintStatus(
            @Parameter(description = "신고 ID (URL 경로에서 전달)", required = true) @PathVariable Long complaintId,
            @Parameter(
                    description = "신고 상태",
                    required = true,
                    schema = @Schema(
                            type = "string",
                            allowableValues = {"PENDING", "RESOLVED", "REJECTED"}
                    )
            ) @RequestParam("status") ComplaintStatus status);
}