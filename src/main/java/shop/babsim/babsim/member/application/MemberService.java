package shop.babsim.babsim.member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.member.api.dto.request.UpdateProfileReqDto;
import shop.babsim.babsim.member.api.dto.response.MemberPageInfoResDto;
import shop.babsim.babsim.member.api.dto.response.MyPageInfoResDto;
import shop.babsim.babsim.member.api.dto.response.ProfileInfoResDto;
import shop.babsim.babsim.member.api.dto.response.ProfileInfoResListDto;
import shop.babsim.babsim.member.api.dto.response.UpdateMyPageInfoResDto;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.notification.domain.repository.NotificationRepository;
import shop.babsim.babsim.review.domain.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;

    // 마이페이지 조회 (사진, 리뷰 수, 제보 수, 평균 평점)
    public MyPageInfoResDto findMyProfileByEmail(String email) {
        return memberRepository.findMyProfileByEmail(email);
    }

    // 상대방 프로필 조회 (사진, 이름, 총 리뷰 수, 평균 평점, 총 제보 수, 리뷰 리스트)
    public MemberPageInfoResDto findProfileByEmail(Long memberId) {
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

    @Transactional
    public void deleteMember(String email) {
        memberRepository.delete(memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new));
    }

    public ProfileInfoResListDto getAvailableProfiles(String email) {
        int reviewCount = reviewRepository.getReviewCountByEmail(email);
        List<ProfileInfoResDto> profiles = generateProfiles(reviewCount);
        return ProfileInfoResListDto.from(profiles);
    }

    private List<ProfileInfoResDto> generateProfiles(int reviewCount) {
        return List.of(
                createProfile(1, "밥심", reviewCount >= -1),
                createProfile(2, "쌀알", reviewCount >= 0),
                createProfile(3, "밥그릇", reviewCount >= 3),
                createProfile(4, "맛잘알", reviewCount >= 10),
                createProfile(5, "밥도둑", reviewCount >= 20),
                createProfile(6, "밥심대장", reviewCount >= 30)
        );
    }

    private ProfileInfoResDto createProfile(int level, String name, boolean unlocked) {
        return ProfileInfoResDto.builder()
                .profileLevel(level)
                .name(name)
                .imageUrl("user-image/" + level + ".png")
                .isLocked(!unlocked)
                .build();
    }
}
