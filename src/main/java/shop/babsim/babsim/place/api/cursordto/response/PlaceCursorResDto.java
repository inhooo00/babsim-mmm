package shop.babsim.babsim.place.api.cursordto.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        List<Map<String, Object>> periods,
        List<String> weekdayDescriptions,
        List<String> photoUrls,
        Double latitude,
        Double longitude,
        Boolean isBookmarked,
        String cursorId,
        Double rating
) {
    private static final ObjectMapper mapper = new ObjectMapper();

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
                .periods(parsePeriods(dto.periods()))
                .weekdayDescriptions(parseStringList(dto.weekdayDescriptions()))
                .photoUrls(parsePhotoUrls(dto.photoUrls()))
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .isBookmarked(dto.isBookmarked())
                .cursorId(dto.placeId())
                .rating(dto.rating())
                .build();
    }

    private static List<String> parseStringList(String input) {
        try {
            return mapper.readValue(input.replace("'", "\""), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static List<Map<String, Object>> parsePeriods(String input) {
        try {
            return mapper.readValue(input.replace("'", "\""), new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static List<String> parsePhotoUrls(String input) {
        if (input == null || input.isBlank()) return Collections.emptyList();

        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(url -> !url.isBlank())
                .toList();
    }
}
