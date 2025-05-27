package shop.babsim.babsim.place.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.place.domain.Place;

@Builder
public record PlaceSearchBookmarkResDto(
        String province,               // 지역
        String city,                   // 시군
        String category,               // 음식 카테고리 (한식, 중식, 양식, 일식, ALL)
        String businessName,           // 가게 이름
        String contactNumber,          // 전화번호
        String address,                // 주소
        String menu1,                  // 첫 번째 메뉴 이름
        String price1,                 // 첫 번째 메뉴 가격
        String menu2,                  // 두 번째 메뉴 이름
        String price2,                 // 두 번째 메뉴 가격
        String placeId,                // 가게 아이디
        String periods,                // 운영 디테일 시간
        String weekdayDescriptions,    // 운영 시간
        String photoUrls,              // 가게 사진
        Double latitude,               // 위도 ex) 37
        Double longitude,              // 경도 ex) 127
        Boolean isBookmarked,          // 북마크 유무
        Double rating                  // 평점
) {
    public static PlaceSearchBookmarkResDto of(Place place, boolean isBookmarked, Double rating) {
        return PlaceSearchBookmarkResDto.builder()
                .province(place.getProvince())
                .city(place.getCity())
                .category(place.getCategory())
                .businessName(place.getBusinessName())
                .contactNumber(place.getContactNumber())
                .address(place.getAddress())
                .menu1(place.getMenu1())
                .price1(place.getPrice1())
                .menu2(place.getMenu2())
                .price2(place.getPrice2())
                .placeId(place.getPlaceId())
                .periods(place.getPeriods())
                .weekdayDescriptions(place.getWeekdayDescriptions())
                .photoUrls(place.getPhotoUrls())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .isBookmarked(isBookmarked)
                .rating(rating != null ? Math.round(rating * 10) / 10.0 : null)
                .build();
    }
}

