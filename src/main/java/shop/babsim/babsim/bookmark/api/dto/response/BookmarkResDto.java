package shop.babsim.babsim.bookmark.api.dto.response;

import shop.babsim.babsim.bookmark.domain.Bookmark;
import shop.babsim.babsim.place.domain.Place;

public record BookmarkResDto(
        String placeId,
        String businessName,
        String rating,
        String menu1,
        String price1,
        String menu2,
        String price2,
        String photoUrls
) {
    public static BookmarkResDto from(Bookmark bookmark, Double averageRating) {
        Place place = bookmark.getPlace();
        return new BookmarkResDto(
                place.getPlaceId(),
                place.getBusinessName(),
                averageRating != null ? String.format("%.1f", averageRating) : "N/A",
                place.getMenu1(),
                place.getPrice1(),
                place.getMenu2(),
                place.getPrice2(),
                place.getPhotoUrls()
        );
    }

    public static BookmarkResDto from(Place place, Double averageRating) {
        return new BookmarkResDto(
                place.getPlaceId(),
                place.getBusinessName(),
                averageRating != null ? String.format("%.1f", averageRating) : "N/A",
                place.getMenu1(),
                place.getPrice1(),
                place.getMenu2(),
                place.getPrice2(),
                place.getPhotoUrls()
        );
    }
}
