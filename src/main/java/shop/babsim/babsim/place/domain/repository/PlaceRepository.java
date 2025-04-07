package shop.babsim.babsim.place.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.babsim.babsim.place.domain.Place;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceCustomRepository {

    Optional<Place> findByBusinessName(String businessName);

    Optional<Place> findByPlaceId(String placeId);
}
