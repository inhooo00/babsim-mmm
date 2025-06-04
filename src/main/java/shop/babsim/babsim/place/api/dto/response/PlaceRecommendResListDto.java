package shop.babsim.babsim.place.api.dto.response;

import java.util.List;

public record PlaceRecommendResListDto(
        List<PlaceRecommendResDto> places
) {
    public static PlaceRecommendResListDto from(List<PlaceRecommendResDto> list) {
        return new PlaceRecommendResListDto(list);
    }
}
