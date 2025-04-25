package shop.babsim.babsim.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.member.api.dto.response.MyPageInfoResDto;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    // 마이페이지 조회 (사진, 리뷰 수, 제보 수, 평균 평점)
    public MyPageInfoResDto findMyProfileByEmail(String email) {
        return memberRepository.findMyProfileByEmail(email);
    }

    // 내가 쓴 리뷰 리스트 조회

    // 내가 쓴 제보 리스트 조회

    // 상대방 프로필 조회 (사진, 이름, 총 리뷰 수, 평균 평점, 총 제보 수, 리뷰 리스트)
}
