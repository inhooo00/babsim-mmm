package shop.babsim.babsim.place.api.dto.response;

import java.util.List;
import lombok.Builder;
import shop.babsim.babsim.global.dto.PageInfoResDto;

@Builder
public record PlaceResListDto(
        List<PlaceSearchBookmarkResDto> placeCsvData,
        PageInfoResDto pageInfoResDto
) {
    public static PlaceResListDto of(List<PlaceSearchBookmarkResDto> placeCsvDatas, PageInfoResDto pageInfoResDto) {
        return PlaceResListDto.builder()
                .placeCsvData(placeCsvDatas)
                .pageInfoResDto(pageInfoResDto)
                .build();
    }
}
