package shop.babsim.babsim.place.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;
import shop.babsim.babsim.review.domain.Review;

@Entity
@Getter
@NoArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String province; // 지역

    private String city; // 시군

    private String category; // 음식 카테고리 (한식, 중식, 양식, 일식, ALL)

    private String businessName; // 가게 이름

    private String contactNumber; // 전화번호

    private String address; // 주소

    private String menu1; // 첫 번째 메뉴 이름

    private String price1; // 첫 번째 메뉴 가격

    private String menu2; // 두 번째 메뉴 이름

    private String price2; // 두 번째 메뉴 가격

    private String placeId; // 가게 아이디

    @Column(columnDefinition = "TEXT")
    private String periods; // 운영 디테일 시간

    @Column(columnDefinition = "TEXT")
    private String weekdayDescriptions; // 운영 시간

    @Column(columnDefinition = "TEXT")
    private String photoUrls; // 가게 사진

    private Double latitude; // 위도 ex) 37

    private Double longitude; // 경도 ex) 127

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public static Place from(PlaceCsvData placeCsvData) {
        Place place = new Place();
        place.province = placeCsvData.getProvince();
        place.city = placeCsvData.getCity();
        place.category = placeCsvData.getCategory();
        place.businessName = placeCsvData.getBusinessName();
        place.contactNumber = placeCsvData.getContactNumber();
        place.address = placeCsvData.getAddress();
        place.menu1 = placeCsvData.getMenu1();
        place.price1 = placeCsvData.getPrice1();
        place.menu2 = placeCsvData.getMenu2();
        place.price2 = placeCsvData.getPrice2();
        place.placeId = placeCsvData.getPlaceId();
        place.periods = placeCsvData.getPeriods();
        place.weekdayDescriptions = placeCsvData.getWeekdayDescriptions();
        place.photoUrls = placeCsvData.getPhotoUrls();
        place.latitude = placeCsvData.getLatitude();
        place.longitude = placeCsvData.getLongitude();
        return place;
    }
}
