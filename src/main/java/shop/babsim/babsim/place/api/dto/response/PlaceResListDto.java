package shop.babsim.babsim.place.api.dto.response;

import java.util.List;
import lombok.Builder;
import shop.babsim.babsim.global.dto.PageInfoResDto;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;

@Builder
public record PlaceResListDto(
        List<PlaceCsvData> placeCsvData,
        PageInfoResDto pageInfoResDto
) {
    public static PlaceResListDto of(List<PlaceCsvData> placeCsvDatas, PageInfoResDto pageInfoResDto) {
        return PlaceResListDto.builder()
                .placeCsvData(placeCsvDatas)
                .pageInfoResDto(pageInfoResDto)
                .build();
    }
}
