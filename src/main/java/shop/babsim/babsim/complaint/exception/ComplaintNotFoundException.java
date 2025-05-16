package shop.babsim.babsim.complaint.exception;

import shop.babsim.babsim.global.error.exception.NotFoundGroupException;

public class ComplaintNotFoundException extends NotFoundGroupException {
    public ComplaintNotFoundException(String message) {
        super(message);
    }

    public ComplaintNotFoundException() {
        this("존재하지 않는 신고입니다");
    }
}
