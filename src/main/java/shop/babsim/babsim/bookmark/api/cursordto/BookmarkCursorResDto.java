package shop.babsim.babsim.bookmark.api.cursordto;

import java.util.List;
import shop.babsim.babsim.bookmark.api.dto.response.BookmarkResDto;

public record BookmarkCursorResDto(
        List<BookmarkResDto> data,
        Long nextCursor,
        boolean hasNext
) {
    public static BookmarkCursorResDto of(List<BookmarkResDto> data, Long nextCursor) {
        return new BookmarkCursorResDto(data, nextCursor, nextCursor != null);
    }
}
