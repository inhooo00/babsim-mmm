package shop.babsim.babsim.place.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.place.api.cursordto.response.PlaceCursorResDto;
import shop.babsim.babsim.place.api.cursordto.response.PlaceCursorResListDto;
import shop.babsim.babsim.place.api.cursordto.response.PlaceSearchCursorResDto;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.api.dto.response.PlaceRecommendResListDto;
import shop.babsim.babsim.place.api.dto.response.PlaceResListDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchBookmarkResDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResListDto;
import shop.babsim.babsim.place.application.PlaceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceController implements PlaceDocs {

    public final PlaceService placeService;

    @GetMapping
    public RspTemplate<PlaceResListDto> getPlaceCsvData(@CurrentUserEmail String email,
                                                        @ModelAttribute LocationCoordinatesDto locationCoordinatesDto,
                                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        return new RspTemplate<>(HttpStatus.OK,
                "장소 데이터 조회 성공",
                placeService.getPlaceCsvData(email, locationCoordinatesDto, PageRequest.of(page, size)));
    }

    @GetMapping("/search")
    public RspTemplate<PlaceSearchResListDto> search(@RequestParam("keyword") String keyword,
                                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                                     @RequestParam(name = "size", defaultValue = "5") int size) {
        return new RspTemplate<>(HttpStatus.OK,
                "장소 검색 성공",
                placeService.getPlacesByMenu(keyword, PageRequest.of(page, size)));
    }

    @GetMapping("/place-id")
    public RspTemplate<PlaceSearchBookmarkResDto> getPlaceCsvDataById(@CurrentUserEmail String email,
                                                                      @RequestParam("placeId") String placeId) {
        return new RspTemplate<>(HttpStatus.OK,
                "장소 개별 조회 성공",
                placeService.getPlaceCsvDataByPlaceId(email, placeId));
    }

    @GetMapping("/cursor")
    public RspTemplate<PlaceCursorResListDto> getPlacesByCursor(
            @CurrentUserEmail String email,
            @RequestParam(required = false) String cursorId,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @ModelAttribute LocationCoordinatesDto locationCoordinatesDto
    ) {
        return new RspTemplate<>(
                HttpStatus.OK,
                "커서 기반 장소 조회 성공",
                placeService.getPlacesByCursor(email, locationCoordinatesDto, cursorId, size)
        );
    }

    @GetMapping("/search/cursor")
    public RspTemplate<PlaceSearchCursorResDto> searchWithCursor(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "cursorId", required = false) String cursorId,
            @RequestParam(value = "size", defaultValue = "5") int size) {

        return new RspTemplate<>(HttpStatus.OK,
                "장소 커서 기반 검색 성공",
                placeService.getPlacesByMenuWithCursor(keyword, cursorId, size));
    }

    @GetMapping("/recommend/random-region")
    public RspTemplate<PlaceRecommendResListDto> getRandomRecommendationsByRegion() {
        return new RspTemplate<>(
                HttpStatus.OK,
                "지역별 랜덤 장소 추천 성공",
                placeService.getRandomPlaceRecommendationsByRegion()
        );
    }

    @GetMapping("/place-id/cursor")
    public RspTemplate<PlaceCursorResDto> getCursorPlaceCsvDataById(@CurrentUserEmail String email,
                                                              @RequestParam("placeId") String placeId) {
        return new RspTemplate<>(HttpStatus.OK,
                "장소 개별 조회 성공",
                placeService.getCursorPlaceCsvDataByPlaceId(email, placeId));
    }
}