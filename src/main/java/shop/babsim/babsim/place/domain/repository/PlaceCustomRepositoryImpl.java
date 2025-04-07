package shop.babsim.babsim.place.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.place.domain.QPlace;

@Repository
@RequiredArgsConstructor
public class PlaceCustomRepositoryImpl implements PlaceCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PlaceCsvData> findAllByLocationCoordinates(LocationCoordinatesDto locationCoordinatesDto,
                                                           Pageable pageable) {
        QPlace place = QPlace.place;

        double minLatitude = locationCoordinatesDto.minLatitude();
        double maxLatitude = locationCoordinatesDto.maxLatitude();
        double minLongitude = locationCoordinatesDto.minLongitude();
        double maxLongitude = locationCoordinatesDto.maxLongitude();

        List<Place> places = queryFactory
                .selectFrom(place)
                .where(
                        place.latitude.between(minLatitude, maxLatitude), // 위도 범위 필터링
                        // 경계선을 넘어가는 경우도 처리
                        (minLongitude <= maxLongitude) ?
                                place.longitude.between(minLongitude, maxLongitude) :
                                place.longitude.loe(minLongitude).or(place.longitude.goe(maxLongitude))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PlaceCsvData> result = places.stream()
                .map(p -> new PlaceCsvData(
                        p.getProvince(),
                        p.getCity(),
                        p.getCategory(),
                        p.getBusinessName(),
                        p.getContactNumber(),
                        p.getAddress(),
                        p.getMenu1(),
                        p.getPrice1(),
                        p.getMenu2(),
                        p.getPrice2(),
                        p.getPlaceId(),
                        p.getPeriods(),
                        p.getWeekdayDescriptions(),
                        p.getPhotoUrls(),
                        p.getLatitude(),
                        p.getLongitude()
                ))
                .toList();

        long total = queryFactory
                .selectFrom(place)
                .where(
                        place.latitude.between(minLatitude, maxLatitude),
                        (minLongitude <= maxLongitude) ?
                                place.longitude.between(minLongitude, maxLongitude) :
                                place.longitude.loe(minLongitude).or(place.longitude.goe(maxLongitude))
                )
                .fetchCount();

        return PageableExecutionUtils.getPage(result, pageable, () -> total);
    }
}
