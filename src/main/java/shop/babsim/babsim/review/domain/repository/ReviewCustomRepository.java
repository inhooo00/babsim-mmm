package shop.babsim.babsim.review.domain.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.babsim.babsim.review.api.dto.response.ReviewInfoResDto;

public interface ReviewCustomRepository {
    Page<ReviewInfoResDto> findAllByPlaceIdExcludingBlocked(String placeId, List<Long> blockedIds,
                                                            Pageable pageable);

    Double getRatingAvgByPlaceId(String placeId);

    Page<ReviewInfoResDto> findAllByMemberId(Long memberId, Pageable pageable);
}