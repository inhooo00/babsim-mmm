package shop.babsim.babsim.place.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.babsim.babsim.place.domain.PlaceCsvHash;

public interface PlaceCsvHashRepository extends JpaRepository<PlaceCsvHash, String> {
}
