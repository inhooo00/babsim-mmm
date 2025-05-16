package shop.babsim.babsim.member.block.exception;

import shop.babsim.babsim.global.error.exception.InvalidGroupException;

public class AlreadyBlockedException extends InvalidGroupException {
    public AlreadyBlockedException(String message) {
        super(message);
    }

    public AlreadyBlockedException() {
        this("이미 차단한 유저입니다.");
    }

}
