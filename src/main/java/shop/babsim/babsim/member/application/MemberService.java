package shop.babsim.babsim.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.member.api.dto.request.UpdateProfileReqDto;
import shop.babsim.babsim.member.api.dto.response.MyPageInfoResDto;
import shop.babsim.babsim.member.api.dto.response.UpdateMyPageInfoResDto;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.review.domain.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    // 마이페이지 조회 (사진, 리뷰 수, 제보 수, 평균 평점)
    public MyPageInfoResDto findMyProfileByEmail(String email) {
        return memberRepository.findProfileByEmail(email);
    }

    // 상대방 프로필 조회 (사진, 이름, 총 리뷰 수, 평균 평점, 총 제보 수, 리뷰 리스트)
    public MyPageInfoResDto findProfileByEmail(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        return memberRepository.findProfileByEmail(member.getEmail());
    }

    // 내 수정 정보 조회
    public UpdateMyPageInfoResDto findMyProfile(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        return UpdateMyPageInfoResDto.of(member, memberRepository.getReviewCountByEmail(email));
    }

    // 내 정보 수정
    @Transactional
    public UpdateMyPageInfoResDto updateMyProfile(String email, UpdateProfileReqDto updateProfileReqDto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        member.updateProfile(updateProfileReqDto.getPicture(), updateProfileReqDto.getNickname());

        return UpdateMyPageInfoResDto.of(member, memberRepository.getReviewCountByEmail(email));
    }
}
