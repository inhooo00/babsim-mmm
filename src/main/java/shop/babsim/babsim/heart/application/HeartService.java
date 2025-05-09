package shop.babsim.babsim.heart.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.heart.domain.repository.HeartRepository;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.notification.application.NotificationService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    @Transactional
    public void toggleReviewHeart(String email, Long reviewId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        Member reviewer = memberRepository.findByReviewId(reviewId)
                .orElseThrow(MemberNotFoundException::new);

        boolean isLiked = !heartRepository.existsByMemberAndReviewId(member, reviewId);

        if (isLiked) {
            heartRepository.addReviewHeart(member, reviewId);
            notificationService.send(reviewer, member.getName() + "님이 리뷰에 좋아요를 눌렀습니다.");
            return;
        }

        heartRepository.removeReviewHeart(member, reviewId);
    }
}
