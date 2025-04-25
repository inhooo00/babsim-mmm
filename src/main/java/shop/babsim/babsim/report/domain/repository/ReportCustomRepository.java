package shop.babsim.babsim.report.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.babsim.babsim.report.api.dto.response.ReportResDto;
import shop.babsim.babsim.review.api.dto.response.ReviewInfoResDto;

public interface ReportCustomRepository {

    Page<ReportResDto> findAllByMemberId(Long memberId, Pageable pageable);
}