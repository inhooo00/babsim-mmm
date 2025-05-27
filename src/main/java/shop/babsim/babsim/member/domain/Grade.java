package shop.babsim.babsim.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Grade {
    BASIC("밥심", 0),
    LEVEL_1("쌀알", 3),
    LEVEL_2("밥그릇", 10),
    LEVEL_3("맛잘알", 20),
    LEVEL_4("밥도둑", 30),
    LEVEL_5("밥심대장", 50);

    private final String name;
    private final int reviewThreshold;

    // 리뷰 개수로 등급 반환
    public static Grade getGradeByReviewCount(int reviewCount) {
        if (reviewCount >= LEVEL_5.reviewThreshold) return LEVEL_5;
        if (reviewCount >= LEVEL_4.reviewThreshold) return LEVEL_4;
        if (reviewCount >= LEVEL_3.reviewThreshold) return LEVEL_3;
        if (reviewCount >= LEVEL_2.reviewThreshold) return LEVEL_2;
        if (reviewCount >= LEVEL_1.reviewThreshold) return LEVEL_1;
        return BASIC;
    }
}
