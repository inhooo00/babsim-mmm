package shop.babsim.babsim.complaint.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.babsim.babsim.complaint.domain.Complaint;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.review.domain.Review;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    boolean existsByMemberAndReview(Member member, Review review);
    List<Complaint> findByReviewId(Long reviewId);
}
