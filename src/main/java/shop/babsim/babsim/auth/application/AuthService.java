package shop.babsim.babsim.auth.application;

import shop.babsim.babsim.auth.api.dto.response.IdTokenResDto;
import shop.babsim.babsim.auth.api.dto.response.UserInfo;

public interface AuthService {
    UserInfo getUserInfo(String authCode);

    String getProvider();

    IdTokenResDto getIdToken(String code);

    void unlink(String accessToken);
}
