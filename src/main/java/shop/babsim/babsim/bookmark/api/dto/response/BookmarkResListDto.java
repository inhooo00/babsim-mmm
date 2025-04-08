package shop.babsim.babsim.bookmark.api.dto.response;

import java.util.List;
import lombok.Builder;
import shop.babsim.babsim.global.dto.PageInfoResDto;

@Builder
public record BookmarkResListDto(
        List<BookmarkResDto> Places,
        PageInfoResDto pageInfoResDto
) {
    public static BookmarkResListDto of(List<BookmarkResDto> placeResDtos, PageInfoResDto pageInfoResDto) {
        return BookmarkResListDto.builder()
                .Places(placeResDtos)
                .pageInfoResDto(pageInfoResDto)
                .build();
    }
}
