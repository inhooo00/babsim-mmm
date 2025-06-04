package shop.babsim.babsim.place.application;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.bookmark.domain.repository.BookmarkRepository;
import shop.babsim.babsim.global.dto.PageInfoResDto;
import shop.babsim.babsim.place.api.cursordto.response.PlaceCursorResListDto;
import shop.babsim.babsim.place.api.cursordto.response.PlaceSearchCursorResDto;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.api.dto.response.PlaceRecommendResDto;
import shop.babsim.babsim.place.api.dto.response.PlaceRecommendResListDto;
import shop.babsim.babsim.place.api.dto.response.PlaceResListDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchBookmarkResDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResListDto;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.place.domain.Region;
import shop.babsim.babsim.place.domain.repository.PlaceRepository;
import shop.babsim.babsim.place.exception.PlaceNotFoundException;
import shop.babsim.babsim.review.domain.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final BookmarkRepository bookmarkRepository;

//    private final PlaceSearchRepository placeSearchRepository;

    public PlaceResListDto getPlaceCsvData(String email, LocationCoordinatesDto locationCoordinatesDto,
                                           Pageable pageable) {
        String safeEmail = (email != null && !email.isBlank()) ? email : null;
        Page<PlaceSearchBookmarkResDto> placeCsvDatas = placeRepository.findAllByLocationCoordinates(safeEmail,
                locationCoordinatesDto, pageable);

        return PlaceResListDto.of(placeCsvDatas.getContent(), PageInfoResDto.from(placeCsvDatas));
    }


    public PlaceSearchResListDto getPlacesByMenu(String keyword, Pageable pageable) {
        Page<PlaceSearchResDto> placeSearchResDtos = placeRepository.searchByKeyword(keyword,
                pageable);

        return PlaceSearchResListDto.of(placeSearchResDtos.getContent(), PageInfoResDto.from(placeSearchResDtos));
    }

    public PlaceSearchBookmarkResDto getPlaceCsvDataByPlaceId(String email, String placeId) {
        Place place = placeRepository.findByPlaceId(placeId)
                .orElseThrow(PlaceNotFoundException::new);

        Double ratingAvg = reviewRepository.getRatingAvgByPlaceId(placeId);
        boolean isBookmarked = (email != null && !email.isBlank()) &&
                bookmarkRepository.isBookmarked(email, placeId);

        return PlaceSearchBookmarkResDto.of(place, isBookmarked, ratingAvg);
    }

    public PlaceCursorResListDto getPlacesByCursor(String email, LocationCoordinatesDto dto, String cursorId,
                                                   int size) {
        List<PlaceSearchBookmarkResDto> rawData = placeRepository.findAllByCursor(email, dto, cursorId, size);

        return PlaceCursorResListDto.of(rawData, size);
    }

    public PlaceSearchCursorResDto getPlacesByMenuWithCursor(String keyword, String cursorId, int size) {
        List<PlaceSearchResDto> rawResults = placeRepository.searchByKeywordWithCursor(keyword, cursorId, size);

        boolean hasNext = rawResults.size() > size;
        List<PlaceSearchResDto> trimmed = hasNext ? rawResults.subList(0, size) : rawResults;

        String nextCursor = hasNext ? trimmed.get(trimmed.size() - 1).placeId() : null;

        return PlaceSearchCursorResDto.of(trimmed, nextCursor);
    }

    public PlaceRecommendResListDto getRandomPlaceRecommendationsByRegion() {
        List<PlaceRecommendResDto> result = Region.all().stream()
                .map(region -> placeRepository.findRandomPlaceByProvince(region.getProvinceName())
                        .map(this::toPlaceRecommendResDto)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return PlaceRecommendResListDto.from(result);
    }

    private PlaceRecommendResDto toPlaceRecommendResDto(Place place) {
        Double rating = reviewRepository.getRatingAvgByPlaceId(place.getPlaceId());

        return new PlaceRecommendResDto(
                place.getProvince(),
                place.getBusinessName(),
                place.getAddress(),
                place.getMenu1(),
                place.getPrice1(),
                place.getPlaceId(),
                rating,
                place.getPhotoUrls()
        );
    }

}
