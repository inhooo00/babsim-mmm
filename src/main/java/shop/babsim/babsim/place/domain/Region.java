package shop.babsim.babsim.place.domain;

import java.util.Arrays;
import java.util.List;

public enum Region {
    SEOUL("서울특별시"),
    GYEONGGI("경기도"),
    GANGWON("강원특별자치도"),
    INCHEON("인천광역시"),
    CHUNGCHEONG_N("충청북도"),
    CHUNGCHEONG_S("충청남도"),
    GYEONGSANG_N("경상북도"),
    GYEONGSANG_S("경상남도"),
    JEJU("제주특별자치도"),
    DAEJEON("대전광역시"),
    DAEGU("대구광역시"),
    GWANGJU("광주광역시"),
    ULSAN("울산광역시"),
    BUSAN("부산광역시");

    private final String provinceName;

    Region(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public static List<Region> all() {
        return Arrays.asList(values());
    }
}
