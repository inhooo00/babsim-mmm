package shop.babsim.babsim.bookmark.api.dto.response;

import shop.babsim.babsim.bookmark.domain.Bookmark;
import shop.babsim.babsim.place.domain.Place;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record BookmarkResDto(
        String placeId,
        Double latitude,
        Double longitude,
        String businessName,
        String rating,
        String menu1,
        String price1,
        String menu2,
        String price2,
        List<String> photoUrls
) {
    public static BookmarkResDto of(Bookmark bookmark, Double averageRating) {
        Place place = bookmark.getPlace();
        return new BookmarkResDto(
                place.getPlaceId(),
                place.getLatitude(),
                place.getLongitude(),
                place.getBusinessName(),
                averageRating != null ? String.format("%.1f", averageRating) : "N/A",
                place.getMenu1(),
                place.getPrice1(),
                place.getMenu2(),
                place.getPrice2(),
                parseCommaSeparatedList(place.getPhotoUrls())
        );
    }

    public static BookmarkResDto of(Place place, Double averageRating) {
        return new BookmarkResDto(
                place.getPlaceId(),
                place.getLatitude(),
                place.getLongitude(),
                place.getBusinessName(),
                averageRating != null ? String.format("%.1f", averageRating) : "N/A",
                place.getMenu1(),
                place.getPrice1(),
                place.getMenu2(),
                place.getPrice2(),
                parseCommaSeparatedList(place.getPhotoUrls())
        );
    }

    private static List<String> parseCommaSeparatedList(String input) {
        if (input == null || input.isBlank()) return Collections.emptyList();

        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
