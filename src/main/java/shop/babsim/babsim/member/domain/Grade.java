package shop.babsim.babsim.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;

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

    public static Grade getGradeByReviewCount(int reviewCount) {
        return Arrays.stream(Grade.values())
                .filter(g -> reviewCount >= g.reviewThreshold)
                .max(Comparator.comparingInt(Grade::getReviewThreshold))
                .orElse(BASIC);
    }

    public Grade getNext() {
        Grade[] values = Grade.values();
        int idx = this.ordinal();
        return (idx + 1 < values.length) ? values[idx + 1] : null;
    }

    public int getMinReviewCount() {
        return this.reviewThreshold;
    }
}
