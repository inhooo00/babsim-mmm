package shop.babsim.babsim.report.domain.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.babsim.babsim.report.domain.Report;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportCustomRepository {

    @Query("""
        SELECT r FROM Report r
        WHERE r.member.id = :memberId
          AND (:cursorId IS NULL OR r.id < :cursorId)
        ORDER BY r.id DESC
    """)
    List<Report> findAllByMemberIdWithCursor(
            @Param("memberId") Long memberId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
