package shop.babsim.babsim.place.exception;

import shop.babsim.babsim.global.error.exception.NotFoundGroupException;

public class PlaceNotFoundException extends NotFoundGroupException {
    public PlaceNotFoundException(String message) {
        super(message);
    }

    public PlaceNotFoundException() {
        this("존재하지 않는 장소입니다");
    }
}
