package shop.babsim.babsim.place.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchBookmarkResDto;

public interface PlaceCustomRepository {

    Page<PlaceSearchBookmarkResDto> findAllByLocationCoordinates(String email,
                                                                 LocationCoordinatesDto locationCoordinatesDto,
                                                                 Pageable pageable);
}

