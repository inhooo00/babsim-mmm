package shop.babsim.babsim.place.api.cursordto.response;

import lombok.Builder;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchBookmarkResDto;

@Builder
public record PlaceCursorResDto(
        String province,
        String city,
        String category,
        String businessName,
        String contactNumber,
        String address,
        String menu1,
        String price1,
        String menu2,
        String price2,
        String placeId,
        String periods,
        String weekdayDescriptions,
        String photoUrls,
        Double latitude,
        Double longitude,
        Boolean isBookmarked,
        String cursorId //
) {
    public static PlaceCursorResDto of(PlaceSearchBookmarkResDto dto) {
        return PlaceCursorResDto.builder()
                .province(dto.province())
                .city(dto.city())
                .category(dto.category())
                .businessName(dto.businessName())
                .contactNumber(dto.contactNumber())
                .address(dto.address())
                .menu1(dto.menu1())
                .price1(dto.price1())
                .menu2(dto.menu2())
                .price2(dto.price2())
                .placeId(dto.placeId())
                .periods(dto.periods())
                .weekdayDescriptions(dto.weekdayDescriptions())
                .photoUrls(dto.photoUrls())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .isBookmarked(dto.isBookmarked())
                .cursorId(dto.placeId())
                .build();
    }
}
