package shop.babsim.babsim.place.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.place.api.cursordto.response.PlaceCursorResListDto;
import shop.babsim.babsim.place.api.cursordto.response.PlaceSearchCursorResDto;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.api.dto.response.PlaceRecommendResListDto;
import shop.babsim.babsim.place.api.dto.response.PlaceResListDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchBookmarkResDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResListDto;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;

@Tag(name = "[장소 API]", description = "장소 관련 API")
public interface PlaceDocs {

    @Operation(summary = "위경도 좌표 2개로 장소 리스트 조회", description = "위경도 좌표 2개로 장소 리스트를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "장소 조회 성공",
                            content = @Content(schema = @Schema(implementation = PlaceSearchBookmarkResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<PlaceResListDto> getPlaceCsvData(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "좌표 2개", required = true) LocationCoordinatesDto locationCoordinatesDto,
            @Parameter(description = "페이지 번호", required = true) int page,
            @Parameter(description = "요청할 개수", required = true) int size
    );

    @Operation(summary = "장소 검색", description = "메뉴 이름 혹은 가게 이름을 검색하여 장소를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "장소 조회 성공",
                            content = @Content(schema = @Schema(implementation = PlaceSearchResListDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<PlaceSearchResListDto> search(
            @Parameter(description = "검색할 키워드 (메뉴 이름, 가게 이름)", required = true) String keyword,
            @Parameter(description = "페이지 번호", required = true) int page,
            @Parameter(description = "요청할 개수", required = true) int size
    );

    @Operation(summary = "장소 아이디로 디테일한 장소 정보 조회", description = "장소 아이디로 디테일한 장소 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "장소 조회 성공",
                            content = @Content(schema = @Schema(implementation = PlaceSearchBookmarkResDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<PlaceSearchBookmarkResDto> getPlaceCsvDataById(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "장소 아이디", required = true) String placeId
    );

    @Operation(
            summary = "커서 기반 장소 리스트 조회",
            description = "위경도 범위 내에서 커서 기반으로 장소 데이터를 조회합니다. " +
                    "첫 요청 시 cursorId 없이 호출하고, 이후 응답에 포함된 nextCursor 값을 cursorId로 사용하세요.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "장소 조회 성공",
                            content = @Content(schema = @Schema(implementation = PlaceCursorResListDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    RspTemplate<PlaceCursorResListDto> getPlacesByCursor(
            @Parameter(description = "로그인한 유저의 이메일(토큰에서 자동 추출)", hidden = true) String email,
            @Parameter(description = "커서 ID (이전 페이지의 마지막 placeId, 첫 요청 시 생략)", required = false) String cursorId,
            @Parameter(description = "요청할 데이터 개수 (기본값: 10)", hidden = true) int size,
            @Parameter(description = "좌표 정보 (min/max 위도, 경도 포함)", required = true) LocationCoordinatesDto locationCoordinatesDto
    );

    @Operation(
            summary = "커서 기반 키워드로 장소 검색",
            description = "메뉴명 또는 가게명을 키워드로 검색하여 커서 기반으로 장소 리스트를 조회합니다. " +
                    "첫 요청 시에는 cursorId를 생략하고, 이후 응답의 nextCursor를 다음 요청의 cursorId로 사용하세요.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "장소 검색 성공",
                            content = @Content(schema = @Schema(implementation = PlaceSearchCursorResDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    RspTemplate<PlaceSearchCursorResDto> searchWithCursor(
            @Parameter(description = "검색 키워드 (가게 이름 또는 메뉴 이름)", required = true) String keyword,
            @Parameter(description = "커서 ID (이전 페이지 마지막 placeId, 첫 요청 시 생략)", required = false) String cursorId,
            @Parameter(description = "요청할 데이터 개수 (기본값: 5)", hidden = true) int size
    );

    @Operation(
            summary = "지역별 랜덤 음식점 추천",
            description = "14개 시도 중 각 지역별로 하나씩 랜덤으로 음식점을 추천합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "지역별 음식점 추천 성공",
                            content = @Content(schema = @Schema(implementation = PlaceRecommendResListDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    RspTemplate<PlaceRecommendResListDto> getRandomRecommendationsByRegion();

}