package shop.babsim.babsim.place.api.dto.response;

import java.util.List;
import lombok.Builder;
import shop.babsim.babsim.global.dto.PageInfoResDto;

@Builder
public record PlaceSearchResListDto(
        List<PlaceSearchResDto> PlaceNames,
        PageInfoResDto pageInfoResDto
) {
    public static PlaceSearchResListDto of(List<PlaceSearchResDto> placeSearchResDtos, PageInfoResDto pageInfoResDto) {
        return PlaceSearchResListDto.builder()
                .PlaceNames(placeSearchResDtos)
                .pageInfoResDto(pageInfoResDto)
                .build();
    }
}
