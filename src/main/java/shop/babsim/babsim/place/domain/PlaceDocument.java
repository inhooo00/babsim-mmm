package shop.babsim.babsim.place.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
@Document(indexName = "place")
@Mapping(mappingPath = "static/elastic-mapping.json")
@Setting(settingPath = "static/elastic-token.json")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceDocument {

    @Id
    @Field(name = "id", type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text)
    private String province; // 지역

    @Field(type = FieldType.Text)
    private String city; // 시군

    @Field(type = FieldType.Text)
    private String category; // 음식 카테고리 (한식, 중식, 양식, 일식, ALL)

    @Field(type = FieldType.Text)
    private String businessName; // 가게 이름

    @Field(type = FieldType.Text)
    private String contactNumber; // 전화번호

    @Field(type = FieldType.Text)
    private String address; // 주소

    @Field(type = FieldType.Text)
    private String menu1; // 첫 번째 메뉴 이름

    @Field(type = FieldType.Text)
    private String price1; // 첫 번째 메뉴 가격

    @Field(type = FieldType.Text)
    private String menu2; // 두 번째 메뉴 이름

    @Field(type = FieldType.Text)
    private String price2; // 두 번째 메뉴 가격

    @Field(type = FieldType.Text)
    private String placeId; // 가게 아이디

    @Field(type = FieldType.Text)
    @Column(columnDefinition = "TEXT")
    private String periods; // 운영 디테일 시간

    @Field(type = FieldType.Text)
    @Column(columnDefinition = "TEXT")
    private String weekdayDescriptions; // 운영 시간

    @Field(type = FieldType.Text)
    @Column(columnDefinition = "TEXT")
    private String photoUrls; // 가게 사진

    @Field(type = FieldType.Double)
    private Double latitude; // 위도 ex) 37

    @Field(type = FieldType.Double)
    private Double longitude; // 경도 ex) 127

    // ✅ 정적 팩토리 메서드 추가
    public static PlaceDocument from(PlaceCsvData placeCsvData) {
        return PlaceDocument.builder()
                .province(placeCsvData.getProvince())
                .city(placeCsvData.getCity())
                .category(placeCsvData.getCategory())
                .businessName(placeCsvData.getBusinessName())
                .contactNumber(placeCsvData.getContactNumber())
                .address(placeCsvData.getAddress())
                .menu1(placeCsvData.getMenu1())
                .price1(placeCsvData.getPrice1())
                .menu2(placeCsvData.getMenu2())
                .price2(placeCsvData.getPrice2())
                .placeId(placeCsvData.getPlaceId())
                .periods(placeCsvData.getPeriods())
                .weekdayDescriptions(placeCsvData.getWeekdayDescriptions())
                .photoUrls(placeCsvData.getPhotoUrls())
                .latitude(placeCsvData.getLatitude())
                .longitude(placeCsvData.getLongitude())
                .build();
    }
}
