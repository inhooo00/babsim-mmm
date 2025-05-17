package shop.babsim.babsim.member.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.member.api.dto.request.UpdateProfileReqDto;
import shop.babsim.babsim.member.api.dto.response.MyPageInfoResDto;
import shop.babsim.babsim.member.api.dto.response.UpdateMyPageInfoResDto;

@Tag(name = "[유저 API]", description = "유저 관련 API")
public interface MemberDocs {

    @Operation(summary = "마이페이지 조회", description = "마이페이지를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "마이페이지 조회 성공",
                            content = @Content(schema = @Schema(implementation = MyPageInfoResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<MyPageInfoResDto> myProfileInfo(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email);

    @Operation(summary = "상대방 마이페이지 조회", description = "상대방 마이페이지를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상대방 마이페이지 조회 성공",
                            content = @Content(schema = @Schema(implementation = MyPageInfoResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<MyPageInfoResDto> memberProfileInfo(
            @Parameter(description = "reviewId", required = true) Long memberId);

    @Operation(summary = "내 수정할 마이페이지 조회", description = "내 수정할 마이페이지 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "마이페이지 조회 성공",
                            content = @Content(schema = @Schema(implementation = UpdateMyPageInfoResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<UpdateMyPageInfoResDto> findMyProfile(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email);

    @Operation(summary = "마이페이지 수정", description = "마이페이지를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공",
                            content = @Content(schema = @Schema(implementation = UpdateMyPageInfoResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<UpdateMyPageInfoResDto> updateMyProfile(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "updateProfileReqDto", required = true) UpdateProfileReqDto updateProfileReqDto);

    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴시킵니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "탈퇴 성공",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<Void> deleteMyProfile(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email);
}