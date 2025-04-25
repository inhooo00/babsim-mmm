package shop.babsim.babsim.member.domain.repository;

import shop.babsim.babsim.member.api.dto.response.MyPageInfoResDto;

public interface MemberCustomRepository {

    MyPageInfoResDto findMyProfileByEmail(String email);
}
