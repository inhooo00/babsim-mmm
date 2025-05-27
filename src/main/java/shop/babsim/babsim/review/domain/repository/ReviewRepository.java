package shop.babsim.babsim.review.domain.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.babsim.babsim.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

     @Query("""
    SELECT r FROM Review r
    WHERE r.place.placeId = :placeId
      AND (:cursorId IS NULL OR r.id < :cursorId)
      AND r.member.id NOT IN (
         SELECT b.blocked.id FROM Block b WHERE b.blocker.email = :email
      )
    ORDER BY r.id DESC
""")
     List<Review> findByPlaceIdExcludingBlockedWithCursor(
             @Param("email") String email,
             @Param("placeId") String placeId,
             @Param("cursorId") Long cursorId,
             Pageable pageable
     );

     @Query("""
    SELECT r FROM Review r
    WHERE r.member.email = :email
      AND (:cursorId IS NULL OR r.id < :cursorId)
    ORDER BY r.id DESC
""")
     List<Review> findByEmailWithCursor(
             @Param("email") String email,
             @Param("cursorId") Long cursorId,
             Pageable pageable
     );

}
