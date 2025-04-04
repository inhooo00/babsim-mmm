package shop.babsim.babsim.auth.exception;

import shop.babsim.babsim.global.error.exception.NotFoundGroupException;

public class TokenNotFoundException extends NotFoundGroupException {
    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException() {
        this("존재하지 않는 토큰입니다.");
    }
}
