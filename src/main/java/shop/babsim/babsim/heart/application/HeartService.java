package shop.babsim.babsim.heart.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.heart.domain.repository.HeartRepository;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void toggleReviewHeart(String email, Long reviewId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        if (!heartRepository.existsByMemberAndReviewId(member, reviewId)) {
            heartRepository.addReviewHeart(member, reviewId);
            return;
        }

        heartRepository.removeReviewHeart(member, reviewId);
    }
}
