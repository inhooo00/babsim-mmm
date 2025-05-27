package shop.babsim.babsim.member.block.api;

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
import shop.babsim.babsim.member.block.api.cursordto.BlockCursorResDto;
import shop.babsim.babsim.member.block.api.dto.request.BlockUserReqDto;
import shop.babsim.babsim.member.block.api.dto.response.BlockListResDto;

@Tag(name = "[유저 차단 API]", description = "유저 차단 관련 API")
public interface BlockDocs {

    @Operation(summary = "유저 차단", description = "유저를 차단합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "유저 차단 성공",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<Void> blockUser(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "차단하고 싶은 유저 id", required = true) BlockUserReqDto blockUserReqDto);

    @Operation(summary = "유저 차단 해제", description = "유저 차단을 해제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "해제 성공",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<Void> unblockUser(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "해제하고 싶은 유저 id", required = true) BlockUserReqDto blockUserReqDto);

    @Operation(summary = "내가 차단한 유저 조회", description = "내가 차단한 유저들을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "해제 성공",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<BlockListResDto> getMyBlockedUsers(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "페이지 번호", required = true) int page,
            @Parameter(description = "요청할 개수", required = true) int size);

    @Operation(
            summary = "커서 기반 내가 차단한 유저 목록 조회",
            description = "내가 차단한 유저들을 커서 기반으로 조회합니다. " +
                    "첫 요청 시 cursorId 없이 호출하고, 이후 응답에 포함된 nextCursor 값을 다음 요청 시 cursorId로 전달합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "차단 유저 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = BlockCursorResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    RspTemplate<BlockCursorResDto> getMyBlockedUsersWithCursor(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "커서 ID (이전 페이지 마지막 차단 ID, 첫 요청 시 생략)", required = false) Long cursorId,
            @Parameter(hidden = true) int size
    );
}