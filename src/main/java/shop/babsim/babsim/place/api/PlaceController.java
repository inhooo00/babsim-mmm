package shop.babsim.babsim.place.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.api.dto.response.PlaceResListDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResListDto;
import shop.babsim.babsim.place.application.PlaceService;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;

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

    @GetMapping("/business-name")
    public RspTemplate<PlaceCsvData> getPlaceCsvDataByBusinessName(@RequestParam("businessName") String businessName) {
        return new RspTemplate<>(HttpStatus.OK,
                "장소 개별 조회 성공",
                placeService.getPlaceCsvDataByBusinessName(businessName));
    }

    @GetMapping("/place-id")
    public RspTemplate<PlaceCsvData> getPlaceCsvDataById(@RequestParam("placeId") String placeId) {
        return new RspTemplate<>(HttpStatus.OK,
                "장소 개별 조회 성공",
                placeService.getPlaceCsvDataByPlaceId(placeId));
    }
}
