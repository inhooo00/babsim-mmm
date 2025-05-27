package shop.babsim.babsim.place.api.cursordto.response;

import java.util.List;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResDto;

public record PlaceSearchCursorResDto(
        List<PlaceSearchResDto> data,
        String nextCursor,
        boolean hasNext
) {
    public static PlaceSearchCursorResDto of(List<PlaceSearchResDto> data, String nextCursor) {
        boolean hasNext = nextCursor != null;
        return new PlaceSearchCursorResDto(data, nextCursor, hasNext);
    }
}
