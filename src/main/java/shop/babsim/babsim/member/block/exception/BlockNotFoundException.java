package shop.babsim.babsim.member.block.exception;

import shop.babsim.babsim.global.error.exception.NotFoundGroupException;

public class BlockNotFoundException extends NotFoundGroupException {
    public BlockNotFoundException(String message) {
        super(message);
    }

    public BlockNotFoundException() {
        this("존재하지 않는 차단입니다");
    }
}
