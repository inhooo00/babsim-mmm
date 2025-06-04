package shop.babsim.babsim.place.api.dto.response;

public record PlaceRecommendResDto(
        String province,
        String businessName,
        String address,
        String menu1,
        String price1,
        String placeId,
        Double rating,
        String photoUrl
) {
}
