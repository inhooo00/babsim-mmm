package shop.babsim.babsim.place.api.dto.request;

public record LocationCoordinatesDto(
        Double minLatitude, // minLatitude → 좌측상단 위도
        Double maxLatitude, // maxLatitude → 우측하단 위도
        Double minLongitude, // minLongitude → 좌측상단 경도
        Double maxLongitude // maxLongitude → 우측하단 경도
){
}
