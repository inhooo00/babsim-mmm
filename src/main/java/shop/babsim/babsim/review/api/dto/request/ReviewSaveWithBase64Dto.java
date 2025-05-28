package shop.babsim.babsim.review.api.dto.request;

import java.util.List;

public record ReviewSaveWithBase64Dto(
        ReviewSaveReqDto reviewSaveReqDto,
        List<String> reviewImages
) {
}
