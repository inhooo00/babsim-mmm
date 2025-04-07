package shop.babsim.babsim.place.csv.dto;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.babsim.babsim.place.domain.Place;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceCsvData {

    private String province; // 지역

    private String city; // 시군

    private String category; // 음식 카테고리 (한식, 중식, 양식, 일식, ALL)

    private String businessName; // 가게 이름

    private String contactNumber; // 전화번호

    private String address; //주소

    private String menu1; // 첫 번째 메뉴 이름

    private String price1; // 첫 번째 메뉴 가격

    private String menu2; // 첫 번째 메뉴 이름

    private String price2; // 두 번째 메뉴 가격

    private String placeId; // 가게 아이디

    private String periods; // 운영 디테일 시간

    private String weekdayDescriptions; // 운영 시간

    private String photoUrls; // 가게 사진

    private Double latitude; // 위도 ex) 37

    private Double longitude; // 경도 ex) 127

    public static List<String> getFieldNames() {
        Field[] declaredFields = PlaceCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }

        return result;
    }

    public static PlaceCsvData of(Place place) {

        return PlaceCsvData.builder()
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
                .build();
    }
}
