package shop.babsim.babsim.place.api.dto.response;

import shop.babsim.babsim.place.domain.Place;

public record PlaceRecommendResDto(
        String province,
        String businessName,
        String address,
        String menu1,
        String price1,
        String placeId,
        Double latitude,
        Double longitude,
        Double rating,
        String photoUrl
) {
    public static PlaceRecommendResDto of(Place place, Double rating) {
        return new PlaceRecommendResDto(
                place.getProvince(),
                place.getBusinessName(),
                place.getAddress(),
                place.getMenu1(),
                place.getPrice1(),
                place.getPlaceId(),
                place.getLatitude(),
                place.getLongitude(),
                rating != null ? Math.round(rating * 10) / 10.0 : null,
                place.getPhotoUrls()
        );
    }
}
