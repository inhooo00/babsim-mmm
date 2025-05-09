package shop.babsim.babsim.member.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.babsim.babsim.member.domain.Member;

public interface MemberRepository extends
        JpaRepository<Member, Long>,
        JpaSpecificationExecutor<Member>,
        MemberCustomRepository {
    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m JOIN m.reviews r WHERE r.id = :reviewId")
    Optional<Member> findByReviewId(@Param("reviewId") Long reviewId);

    boolean existsByNickname(String nickname);
}
