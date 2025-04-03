package shop.babsim.babsim.member.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import shop.babsim.babsim.member.domain.Member;

public interface MemberRepository extends
        JpaRepository<Member, Long>,
        JpaSpecificationExecutor<Member>,
        MemberCustomRepository {
    Optional<Member> findByEmail(String email);

    boolean existsByNickname(String nickname);
}
