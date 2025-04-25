package shop.babsim.babsim.review.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.api.dto.response.PlaceResListDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResListDto;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;
import shop.babsim.babsim.review.api.dto.response.ReviewInfoResDto;
import shop.babsim.babsim.review.api.dto.response.ReviewListResDto;
import shop.babsim.babsim.review.api.dto.response.ReviewSaveInfoResDto;

@Tag(name = "[장소 리뷰 API]", description = "장소 리뷰 관련 API")
public interface ReviewDocs {

    @Operation(summary = "리뷰 등록", description = "리뷰를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "리뷰 등록 성공",
                            content = @Content(schema = @Schema(implementation = ReviewSaveInfoResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<ReviewSaveInfoResDto> save(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "{\n"
                    + "    \"rating\": 1,\n"
                    + "    \"content\": \"사진동아리 구인\",\n"
                    + "\"placeId\":\"ChIJOxTypzj7ZTUREmXR3yLXUdE\"}\n"
                    + "형식으로 데이터를 보내주세요.", required = true) String reviewSaveReqDtoJson,
            @Parameter(description = "RequestPart로 이미지들을 보내주세요.", required = true) List<MultipartFile> reviewImage
    );

    @Operation(summary = "리뷰 디테일 개별 조회", description = "리뷰 아이디로 리뷰 디테일 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공",
                            content = @Content(schema = @Schema(implementation = ReviewInfoResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<ReviewInfoResDto> getReviewDetail(
            @Parameter(description = "reviewId", required = true) Long reviewId
    );

    @Operation(summary = "place Id로 리뷰 리스트 조회", description = "place Id로 리뷰 리스트를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공",
                            content = @Content(schema = @Schema(implementation = ReviewListResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<ReviewListResDto> getReviewList(
            @Parameter(description = "placeId", required = true) String placeId,
            @Parameter(description = "페이지 번호", required = true) int page,
            @Parameter(description = "요청할 개수", required = true) int size
    );

    @Operation(summary = "내 리뷰 리스트 조회", description = "내 리뷰 리스트를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공",
                            content = @Content(schema = @Schema(implementation = ReviewListResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<ReviewListResDto> getMyReviewList(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "페이지 번호", required = true) int page,
            @Parameter(description = "요청할 개수", required = true) int size
    );
}