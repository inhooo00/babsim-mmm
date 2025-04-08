package shop.babsim.babsim.review.exception;

import shop.babsim.babsim.global.error.exception.NotFoundGroupException;

public class ReviewNotFoundException extends NotFoundGroupException {
    public ReviewNotFoundException(String message) {
        super(message);
    }

    public ReviewNotFoundException() {
        this("존재하지 않는 리뷰입니다");
    }
}
