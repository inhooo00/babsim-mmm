package shop.babsim.babsim.place.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;

public interface PlaceCustomRepository {

    Page<PlaceCsvData> findAllByLocationCoordinates(LocationCoordinatesDto locationCoordinatesDto, Pageable pageable);
}
