package shop.babsim.babsim.place.api.dto.response;

import lombok.Builder;

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
        Boolean isBookmarked           // 북마크 유무
) {

}

