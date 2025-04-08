package shop.babsim.babsim.place.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.global.dto.PageInfoResDto;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.api.dto.response.PlaceResListDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResListDto;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.place.domain.repository.PlaceRepository;
import shop.babsim.babsim.place.domain.repository.elasticsearch.PlaceSearchRepository;
import shop.babsim.babsim.place.exception.PlaceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceSearchRepository placeSearchRepository;

    public PlaceResListDto getPlaceCsvData(LocationCoordinatesDto locationCoordinatesDto, Pageable pageable) {
        Page<PlaceCsvData> placeCsvDatas = placeRepository.findAllByLocationCoordinates(locationCoordinatesDto, pageable);

        return PlaceResListDto.of(placeCsvDatas.getContent(), PageInfoResDto.from(placeCsvDatas));
    }

    public PlaceSearchResListDto getPlacesByMenu(String keyword, Pageable pageable) {
        Page<PlaceSearchResDto> placeSearchResDtos = placeSearchRepository.searchByMenuOrBusinessName(keyword, pageable);

        return PlaceSearchResListDto.of(placeSearchResDtos.getContent(), PageInfoResDto.from(placeSearchResDtos));
    }

    public PlaceCsvData getPlaceCsvDataByBusinessName(String businessName) {
        Place place = placeRepository.findByBusinessName(businessName).orElseThrow(PlaceNotFoundException::new);

        return PlaceCsvData.of(place);
    }

    public PlaceCsvData getPlaceCsvDataByPlaceId(String placeId) {
        Place place = placeRepository.findByPlaceId(placeId).orElseThrow(PlaceNotFoundException::new);

        return PlaceCsvData.of(place);
    }
}
