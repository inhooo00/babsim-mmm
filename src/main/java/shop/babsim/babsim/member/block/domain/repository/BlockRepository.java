package shop.babsim.babsim.member.block.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.babsim.babsim.member.block.domain.Block;

public interface BlockRepository extends JpaRepository<Block, Long> {
    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
    List<Block> findAllByBlockerId(Long blockerId);
    List<Block> findAllByBlockedId(Long blockedId);
}

