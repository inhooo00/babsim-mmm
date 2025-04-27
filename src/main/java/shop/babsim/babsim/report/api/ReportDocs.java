package shop.babsim.babsim.report.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.member.api.dto.response.MyPageInfoResDto;
import shop.babsim.babsim.report.api.dto.request.ReportReqDto;
import shop.babsim.babsim.report.api.dto.response.ReportListResDto;
import shop.babsim.babsim.report.api.dto.response.ReportResDto;

@Tag(name = "[제보 API]", description = "제보 관련 API")
public interface ReportDocs {

    @Operation(summary = "제보하기", description = "제보합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "제보 성공",
                            content = @Content(schema = @Schema(implementation = ReportResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<ReportResDto> saveReport(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "ReportReqDto", required = true) ReportReqDto reportReqDto
    );

    @Operation(summary = "내가 쓴 제보 조회", description = "내가 쓴 제보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "제보 조회 성공",
                            content = @Content(schema = @Schema(implementation = ReportListResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<ReportListResDto> findMyReports(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "페이지 번호", required = true) int page,
            @Parameter(description = "요청할 개수", required = true) int size
    );
}