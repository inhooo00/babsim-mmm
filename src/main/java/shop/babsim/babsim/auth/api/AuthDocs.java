package shop.babsim.babsim.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import shop.babsim.babsim.auth.api.dto.request.IdTokenAndRefreshTokenDto;
import shop.babsim.babsim.auth.api.dto.request.RefreshTokenReqDto;
import shop.babsim.babsim.auth.api.dto.request.TokenReqDto;
import shop.babsim.babsim.auth.api.dto.response.IdTokenResDto;
import shop.babsim.babsim.global.jwt.api.dto.TokenDto;
import shop.babsim.babsim.global.template.RspTemplate;

@Tag(name = "[인증 API]", description = "인증 관련 API")
public interface AuthDocs {

    @Operation(summary = "code로 idToken과 {Provider}의 refreshToken을 발급", description = "code로 idToken과 {Provider}의 refreshToken을 발급받습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 발급 성공",
                            content = @Content(schema = @Schema(implementation = IdTokenAndRefreshTokenDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    IdTokenAndRefreshTokenDto callback(
            @Parameter(description = "OAuth2 제공자 (예: kakao, google)", required = true) String provider,
            @Parameter(description = "code 데이터", required = true) String code);

    @Operation(summary = "액세스 및 리프레시 토큰 발급", description = "회원 정보로부터 액세스 및 리프레시 토큰을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 발급 성공",
                            content = @Content(schema = @Schema(implementation = TokenDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<TokenDto> generateAccessAndRefreshToken(
            @Parameter(description = "OAuth2 제공자 (예: kakao, google)", required = true) String provider,
            @Parameter(description = "토큰 요청 데이터", required = true) TokenReqDto tokenReqDto);

    @Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰을 이용해 새로운 액세스 토큰을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "액세스 토큰 발급 성공",
                            content = @Content(schema = @Schema(implementation = TokenDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<TokenDto> generateAccessToken(
            @Parameter(description = "리프레시 토큰 요청 데이터", required = true) RefreshTokenReqDto refreshTokenReqDto);

    @Operation(
            summary = "소셜 계정 연동 해제 (탈퇴)",
            description = """
                    현재 로그인된 사용자의 소셜 계정(Kakao 등) 연동을 해제하고 회원 정보를 삭제합니다.
                    <br/> Authorization 헤더에는 자체 발급 JWT Access Token을 사용하고,
                    <br/> 바디에는 OAuth Provider로부터 받은 refresh_token을 전달해야 합니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "연결 해제 성공",
                            content = @Content(schema = @Schema(implementation = RspTemplate.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    RspTemplate<String> unlinkSocial(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email
    );
}