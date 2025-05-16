package shop.babsim.babsim.complaint.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.complaint.api.dto.request.ReviewReportReqDto;
import shop.babsim.babsim.complaint.domain.Complaint;
import shop.babsim.babsim.complaint.domain.ComplaintStatus;
import shop.babsim.babsim.complaint.domain.ComplaintType;
import shop.babsim.babsim.complaint.domain.repository.ComplaintRepository;
import shop.babsim.babsim.complaint.exception.ComplaintNotFoundException;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.review.domain.Review;
import shop.babsim.babsim.review.domain.repository.ReviewRepository;
import shop.babsim.babsim.review.exception.ReviewNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComplaintService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ComplaintRepository complaintRepository;

    // 리뷰 신고 생성
    @Transactional
    public void reportReview(String email, ReviewReportReqDto reportReqDto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        Review review = reviewRepository.findById(reportReqDto.reviewId())
                .orElseThrow(ReviewNotFoundException::new);

        // 중복 신고 방지
        if (complaintRepository.existsByMemberAndReview(member, review)) {
            throw new IllegalArgumentException("이미 신고한 리뷰입니다.");
        }

        Complaint complaint = new Complaint(member, review, reportReqDto.type(), reportReqDto.reason());
        complaintRepository.save(complaint);
    }

    // 특정 리뷰에 대한 모든 신고 조회
    public List<Complaint> getComplaintsByReviewId(Long reviewId) {
        return complaintRepository.findByReviewId(reviewId);
    }

    // 신고 처리 상태 업데이트
    @Transactional
    public void resolveComplaint(Long complaintId, ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(ComplaintNotFoundException::new);
        complaint.changeStatus(status);
    }
}

