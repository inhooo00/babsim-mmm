package shop.babsim.babsim.place.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.api.dto.response.PlaceResListDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResListDto;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;

@Tag(name = "[장소 API]", description = "장소 관련 API")
public interface PlaceDocs {

    @Operation(summary = "위경도 좌표 2개로 장소 리스트 조회", description = "위경도 좌표 2개로 장소 리스트를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "장소 조회 성공",
                            content = @Content(schema = @Schema(implementation = PlaceResListDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<PlaceResListDto> getPlaceCsvData(
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

    @Operation(summary = "가게 이름으로 디테일한 장소 정보 조회", description = "가게 이름으로 디테일한 장소 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "장소 조회 성공",
                            content = @Content(schema = @Schema(implementation = PlaceCsvData.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<PlaceCsvData> getPlaceCsvDataByBusinessName(
            @Parameter(description = "가게 이름", required = true) String businessName
    );

    @Operation(summary = "장소 아이디로 디테일한 장소 정보 조회", description = "장소 아이디로 디테일한 장소 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "장소 조회 성공",
                            content = @Content(schema = @Schema(implementation = PlaceCsvData.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    RspTemplate<PlaceCsvData> getPlaceCsvDataById(
            @Parameter(description = "장소 아이디", required = true) String placeId
    );
}