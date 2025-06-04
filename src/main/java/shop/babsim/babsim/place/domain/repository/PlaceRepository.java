package shop.babsim.babsim.place.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResDto;
import shop.babsim.babsim.place.domain.Place;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceCustomRepository {

    Optional<Place> findByBusinessName(String businessName);

    Optional<Place> findByPlaceId(String placeId);

    @Query("""
                SELECT p.businessName AS businessName, p.placeId AS placeId
                FROM Place p
                WHERE LOWER(p.menu1) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.menu2) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.businessName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<PlaceSearchResDto> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM place WHERE province = :province ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Place> findRandomPlaceByProvince(@Param("province") String province);

}
