package shop.babsim.babsim.place.domain.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchBookmarkResDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResDto;

public interface PlaceCustomRepository {

    Page<PlaceSearchBookmarkResDto> findAllByLocationCoordinates(String email,
                                                                 LocationCoordinatesDto locationCoordinatesDto,
                                                                 Pageable pageable);
    List<PlaceSearchBookmarkResDto> findAllByCursor(String email, LocationCoordinatesDto location, String cursorId, int size);
    List<PlaceSearchResDto> searchByKeywordWithCursor(String keyword, String cursor, int size);

}

