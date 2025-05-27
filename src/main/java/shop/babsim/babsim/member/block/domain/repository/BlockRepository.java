package shop.babsim.babsim.member.block.domain.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.babsim.babsim.member.block.domain.Block;

public interface BlockRepository extends JpaRepository<Block, Long> {
    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
    List<Block> findAllByBlockerId(Long blockerId);
    Page<Block> findAllByBlockerId(Long blockerId, Pageable pageable);
    List<Block> findAllByBlockedId(Long blockedId);
    @Query("SELECT b.blocked.id FROM Block b WHERE b.blocker.email = :email")
    List<Long> findBlockedMemberIdsByEmail(@Param("email") String email);

    @Query("""
    SELECT b FROM Block b
    WHERE b.blocker.id = :blockerId
      AND (:cursorId IS NULL OR b.id > :cursorId)
    ORDER BY b.id ASC
""")
    List<Block> findByBlockerIdWithCursor(@Param("blockerId") Long blockerId,
                                          @Param("cursorId") Long cursorId,
                                          Pageable pageable);

}

