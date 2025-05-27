package shop.babsim.babsim.review.api.cursordto;

import java.util.List;
import shop.babsim.babsim.review.api.dto.response.ReviewInfoResDto;

public record ReviewCursorResDto(
        List<ReviewInfoResDto> data,
        Long nextCursor,
        boolean hasNext
) {
    public static ReviewCursorResDto of(List<ReviewInfoResDto> data, Long nextCursor) {
        return new ReviewCursorResDto(data, nextCursor, nextCursor != null);
    }
}
