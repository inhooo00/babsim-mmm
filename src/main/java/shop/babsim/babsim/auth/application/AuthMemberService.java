package shop.babsim.babsim.auth.application;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.auth.api.dto.response.MemberLoginResDto;
import shop.babsim.babsim.auth.api.dto.response.UserInfo;
import shop.babsim.babsim.auth.exception.EmailNotFoundException;
import shop.babsim.babsim.auth.exception.ExistsMemberEmailException;
import shop.babsim.babsim.global.entity.Status;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.Role;
import shop.babsim.babsim.member.domain.SocialType;
import shop.babsim.babsim.member.domain.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthMemberService {

    private final MemberRepository memberRepository;
    private static final String DEFAULT_PROFILE_IMAGE = "/user-image/1.png";


    @Transactional
    public MemberLoginResDto saveUserInfo(UserInfo userInfo, SocialType provider) {
        validateNotFoundEmail(userInfo.email());

        Member member = getExistingMemberOrCreateNew(userInfo, provider);

        validateSocialType(member, provider);

        return MemberLoginResDto.from(member);
    }

    private void validateNotFoundEmail(String email) {
        if (email == null) {
            throw new EmailNotFoundException();
        }
    }

    private Member getExistingMemberOrCreateNew(UserInfo userInfo, SocialType provider) {
        return memberRepository.findByEmail(userInfo.email())
                .orElseGet(() -> createMember(userInfo, provider));
    }

    private Member createMember(UserInfo userInfo, SocialType provider) {
        String name = unionName(userInfo.name(), userInfo.nickname());

        return memberRepository.save(
                Member.builder()
                        .status(Status.ACTIVE)
                        .email(userInfo.email())
                        .name(name)
                        .picture(DEFAULT_PROFILE_IMAGE)
                        .socialType(provider)
                        .role(Role.ROLE_USER)
                        .nickname(name)
                        .introduction("")
                        .build()
        );
    }

    private String unionName(String name, String nickname) {
        return nickname != null ? nickname : name;
    }

    private String getUserPicture(String picture) {
        return Optional.ofNullable(picture)
                .map(this::convertToHighRes)
                .orElse("https://ifh.cc/v-hVmXhH");
    }

    private String convertToHighRes(String url) {
        return url.replace("s96-c", "s2048-c");
    }

    private void validateSocialType(Member member, SocialType provider) {
        if (!provider.equals(member.getSocialType())) {
            throw new ExistsMemberEmailException();
        }
    }
}
