package shop.babsim.babsim.bookmark.exception;

import shop.babsim.babsim.global.error.exception.NotFoundGroupException;

public class BookmarkNotFoundException extends NotFoundGroupException {
    public BookmarkNotFoundException(String message) {
        super(message);
    }

    public BookmarkNotFoundException() {
        this("존재하지 않는 찜입니다");
    }
}
