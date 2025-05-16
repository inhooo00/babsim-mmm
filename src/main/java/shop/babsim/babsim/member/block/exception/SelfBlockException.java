package shop.babsim.babsim.member.block.exception;

import shop.babsim.babsim.global.error.exception.InvalidGroupException;

public class SelfBlockException extends InvalidGroupException {
    public SelfBlockException(String message) {
        super(message);
    }

    public SelfBlockException() {
        this("자기 자신을 차단할 수 없습니다.");
    }
}
