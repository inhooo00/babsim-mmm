package shop.babsim.babsim.bookmark.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import shop.babsim.babsim.auth.api.dto.request.RefreshTokenReqDto;
import shop.babsim.babsim.auth.api.dto.request.TokenReqDto;
import shop.babsim.babsim.auth.api.dto.response.IdTokenResDto;
import shop.babsim.babsim.bookmark.api.dto.response.BookmarkResListDto;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.jwt.api.dto.TokenDto;
import shop.babsim.babsim.global.template.RspTemplate;

@Tag(name = "[리뷰 찜 API]", description = "리뷰 찜 관련 API")
public interface BookmarkDocs {

    @Operation(summary = "리뷰 찜 등록 / 삭제", description = "리뷰 찜 등록 / 삭제를 진행합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "리뷰 찜 등록 / 삭제 성공",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<Void> createOrDeleteBookmark(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "placeId", required = true) String placeId);

    @Operation(summary = "찜 리스트 조회", description = "내 찜 리스트를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "찜 리스트 조회 성공",
                            content = @Content(schema = @Schema(implementation = BookmarkResListDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<BookmarkResListDto> findMyBookmarkList(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "페이지 번호", required = true) int page,
            @Parameter(description = "요청할 개수", required = true) int size
    );

    @Operation(summary = "찜 개수 기준 맛집 추천 조회", description = "찜 개수 기준 맛집 추천 리스트를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "리스트 조회 성공",
                            content = @Content(schema = @Schema(implementation = BookmarkResListDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<BookmarkResListDto> findTopPlacesByBookmarkCount(
            @Parameter(description = "페이지 번호", required = true) int page,
            @Parameter(description = "요청할 개수", required = true) int size
    );
}