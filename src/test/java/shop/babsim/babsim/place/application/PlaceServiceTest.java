package shop.babsim.babsim.place.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchBookmarkResDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResDto;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.place.domain.repository.PlaceRepository;
//import shop.babsim.babsim.place.domain.repository.elasticsearch.PlaceSearchRepository;
import shop.babsim.babsim.place.exception.PlaceNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

//    @Mock
//    private PlaceSearchRepository placeSearchRepository;

    @InjectMocks
    private PlaceService placeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("위경도 기반 장소 목록을 정상적으로 반환한다")
    void getPlaceCsvData_success() {
        String email = "test@example.com";
        LocationCoordinatesDto coords = new LocationCoordinatesDto(37.0, 127.0, 37.5, 127.5);
        Pageable pageable = PageRequest.of(0, 10);

        Page<PlaceSearchBookmarkResDto> fakePage = new PageImpl<>(Collections.emptyList());
        when(placeRepository.findAllByLocationCoordinates(email,coords, pageable)).thenReturn(fakePage);

        var result = placeService.getPlaceCsvData(email,coords, pageable);

        assertThat(result).isNotNull();
        verify(placeRepository).findAllByLocationCoordinates(email,coords, pageable);
    }

    @Test
    @DisplayName("키워드 기반 검색 결과를 정상적으로 반환한다")
    void getPlacesByMenu_success() {
        String keyword = "커피";
        Pageable pageable = PageRequest.of(0, 10);

        Page<PlaceSearchResDto> fakePage = new PageImpl<>(Collections.emptyList());
        when(placeRepository.searchByKeyword(keyword, pageable)).thenReturn(fakePage);

        var result = placeService.getPlacesByMenu(keyword, pageable);

        assertThat(result).isNotNull();
        verify(placeRepository).searchByKeyword(keyword, pageable);
    }

    @Test
    @DisplayName("사업장명으로 조회 시 존재하면 반환한다")
    void getPlaceCsvDataByBusinessName_success() {
        String name = "스타벅스";
        Place place = mock(Place.class);
        when(placeRepository.findByBusinessName(name)).thenReturn(Optional.of(place));

        var result = placeService.getPlaceCsvDataByBusinessName(name);

        assertThat(result).isNotNull();
        verify(placeRepository).findByBusinessName(name);
    }

    @Test
    @DisplayName("사업장명으로 조회 시 없으면 예외 발생")
    void getPlaceCsvDataByBusinessName_notFound() {
        String name = "없는가게";
        when(placeRepository.findByBusinessName(name)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> placeService.getPlaceCsvDataByBusinessName(name))
                .isInstanceOf(PlaceNotFoundException.class);
    }

    @Test
    @DisplayName("placeId로 조회 시 존재하면 반환한다")
    void getPlaceCsvDataByPlaceId_success() {
        String id = "abc123";
        Place place = mock(Place.class);
        when(placeRepository.findByPlaceId(id)).thenReturn(Optional.of(place));

        var result = placeService.getPlaceCsvDataByPlaceId(id);

        assertThat(result).isNotNull();
        verify(placeRepository).findByPlaceId(id);
    }

    @Test
    @DisplayName("placeId로 조회 시 없으면 예외 발생")
    void getPlaceCsvDataByPlaceId_notFound() {
        String id = "없는아이디";
        when(placeRepository.findByPlaceId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> placeService.getPlaceCsvDataByPlaceId(id))
                .isInstanceOf(PlaceNotFoundException.class);
    }
}
