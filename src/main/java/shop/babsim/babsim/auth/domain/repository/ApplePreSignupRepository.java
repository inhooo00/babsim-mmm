package shop.babsim.babsim.auth.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.babsim.babsim.auth.domain.ApplePreSignup;

public interface ApplePreSignupRepository extends JpaRepository<ApplePreSignup, Long> {
    boolean existsBySub(String sub);
    Optional<ApplePreSignup> findBySub(String sub);
}
