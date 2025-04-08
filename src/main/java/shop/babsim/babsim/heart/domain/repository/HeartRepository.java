package shop.babsim.babsim.heart.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.babsim.babsim.heart.domain.Heart;

public interface HeartRepository extends JpaRepository<Heart, Long>, HeartCustomRepository {
}
