package shop.babsim.babsim.place.api.cursordto.response;

import java.util.List;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchBookmarkResDto;

public record PlaceCursorResListDto(
        List<PlaceCursorResDto> data,
        String nextCursor,
        boolean hasNext
) {
    public static PlaceCursorResListDto of(List<PlaceSearchBookmarkResDto> rawData, int size) {
        List<PlaceCursorResDto> converted = rawData.stream()
                .map(PlaceCursorResDto::of)
                .toList();

        boolean hasNext = rawData.size() == size;
        String nextCursor = hasNext ? rawData.get(rawData.size() - 1).placeId() : null;

        return new PlaceCursorResListDto(converted, nextCursor, hasNext);
    }
}


