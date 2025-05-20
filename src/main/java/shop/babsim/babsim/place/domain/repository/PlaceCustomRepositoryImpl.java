package shop.babsim.babsim.place.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import shop.babsim.babsim.bookmark.domain.QBookmark;
import shop.babsim.babsim.member.domain.QMember;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchBookmarkResDto;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.place.domain.QPlace;

@Repository
@RequiredArgsConstructor
public class PlaceCustomRepositoryImpl implements PlaceCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PlaceSearchBookmarkResDto> findAllByLocationCoordinates(String email, LocationCoordinatesDto locationCoordinatesDto,
                                                                        Pageable pageable) {
        QPlace place = QPlace.place;
        QBookmark bookmark = QBookmark.bookmark;
        QMember member = QMember.member;

        double minLatitude = locationCoordinatesDto.minLatitude();
        double maxLatitude = locationCoordinatesDto.maxLatitude();
        double minLongitude = locationCoordinatesDto.minLongitude();
        double maxLongitude = locationCoordinatesDto.maxLongitude();

        List<Place> places = queryFactory
                .selectFrom(place)
                .where(
                        place.latitude.between(minLatitude, maxLatitude),
                        (minLongitude <= maxLongitude) ?
                                place.longitude.between(minLongitude, maxLongitude) :
                                place.longitude.loe(minLongitude).or(place.longitude.goe(maxLongitude))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<String> bookmarkedPlaceIds = (email != null && !email.isBlank())
                ? queryFactory
                .select(bookmark.place.placeId)
                .from(bookmark)
                .join(bookmark.member, member)
                .where(member.email.eq(email))
                .fetch()
                : List.of();

        List<PlaceSearchBookmarkResDto> result = places.stream()
                .map(p -> new PlaceSearchBookmarkResDto(
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
                        p.getLongitude(),
                        bookmarkedPlaceIds.contains(p.getPlaceId()) // false if empty list
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
