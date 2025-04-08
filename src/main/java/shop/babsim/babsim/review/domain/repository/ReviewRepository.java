package shop.babsim.babsim.review.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.babsim.babsim.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {
}
