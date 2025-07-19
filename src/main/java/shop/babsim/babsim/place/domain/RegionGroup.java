package shop.babsim.babsim.place.domain;

import java.util.Arrays;
import java.util.List;

public enum RegionGroup {
    SEOUL(List.of(Region.SEOUL)),
    INCHEON_GYEONGGI(List.of(Region.INCHEON, Region.GYEONGGI)),
    GANGWON_DAEGU(List.of(Region.GANGWON, Region.DAEGU)),
    GWANGJU_JEOLLA(List.of(Region.GWANGJU, Region.JEOLLABUK, Region.JEOLLANAM)),
    DAEJEON_CHUNGCHEONG_SEJONG(List.of(Region.DAEJEON, Region.SEJONG, Region.CHUNGCHEONG_N, Region.CHUNGCHEONG_S)),
    BUSAN_ULSAN_GYEONGSANG(List.of(Region.BUSAN, Region.ULSAN, Region.GYEONGSANG_N, Region.GYEONGSANG_S)),
    JEJU(List.of(Region.JEJU));

    private final List<Region> regions;

    RegionGroup(List<Region> regions) {
        this.regions = regions;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public static List<RegionGroup> all() {
        return Arrays.asList(values());
    }
}
