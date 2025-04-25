package shop.babsim.babsim.report.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.babsim.babsim.report.domain.Report;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportCustomRepository {
}
