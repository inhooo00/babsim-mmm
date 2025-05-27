package shop.babsim.babsim.place.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import shop.babsim.babsim.bookmark.domain.QBookmark;
import shop.babsim.babsim.member.domain.QMember;
import shop.babsim.babsim.place.api.dto.request.LocationCoordinatesDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchBookmarkResDto;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResDto;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.place.domain.QPlace;
import shop.babsim.babsim.review.domain.QReview;

@Repository
@RequiredArgsConstructor
public class PlaceCustomRepositoryImpl implements PlaceCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PlaceSearchBookmarkResDto> findAllByLocationCoordinates(String email,
                                                                        LocationCoordinatesDto locationCoordinatesDto,
                                                                        Pageable pageable) {
        QPlace place = QPlace.place;
        QBookmark bookmark = QBookmark.bookmark;
        QMember member = QMember.member;
        QReview review = QReview.review;

        double minLatitude = locationCoordinatesDto.minLatitude();
        double maxLatitude = locationCoordinatesDto.maxLatitude();
        double minLongitude = locationCoordinatesDto.minLongitude();
        double maxLongitude = locationCoordinatesDto.maxLongitude();

        List<Place> places = queryFactory
                .selectFrom(place)
                .where(
                        place.latitude.between(minLatitude, maxLatitude),
                        (minLongitude <= maxLongitude)
                                ? place.longitude.between(minLongitude, maxLongitude)
                                : place.longitude.loe(minLongitude).or(place.longitude.goe(maxLongitude))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<String> placeIds = places.stream()
                .map(Place::getPlaceId)
                .toList();

        Map<String, Double> ratingMap = queryFactory
                .select(review.place.placeId, review.rating.avg())
                .from(review)
                .where(review.place.placeId.in(placeIds))
                .groupBy(review.place.placeId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(review.place.placeId),
                        tuple -> Optional.ofNullable(tuple.get(review.rating.avg())).orElse(0.0)
                ));

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
                        bookmarkedPlaceIds.contains(p.getPlaceId()),
                        ratingMap.getOrDefault(p.getPlaceId(), 0.0)
                ))
                .toList();

        long total = queryFactory
                .selectFrom(place)
                .where(
                        place.latitude.between(minLatitude, maxLatitude),
                        (minLongitude <= maxLongitude)
                                ? place.longitude.between(minLongitude, maxLongitude)
                                : place.longitude.loe(minLongitude).or(place.longitude.goe(maxLongitude))
                )
                .fetchCount();

        return PageableExecutionUtils.getPage(result, pageable, () -> total);
    }

    @Override
    public List<PlaceSearchBookmarkResDto> findAllByCursor(String email, LocationCoordinatesDto location,
                                                           String cursorId, int size) {
        QPlace place = QPlace.place;
        QBookmark bookmark = QBookmark.bookmark;
        QMember member = QMember.member;
        QReview review = QReview.review;

        BooleanBuilder builder = new BooleanBuilder()
                .and(place.latitude.between(location.minLatitude(), location.maxLatitude()))
                .and((location.minLongitude() <= location.maxLongitude())
                        ? place.longitude.between(location.minLongitude(), location.maxLongitude())
                        : place.longitude.loe(location.minLongitude()).or(place.longitude.goe(location.maxLongitude()))
                );

        if (cursorId != null) {
            builder.and(place.placeId.lt(cursorId));
        }

        List<Place> places = queryFactory
                .selectFrom(place)
                .where(builder)
                .orderBy(place.placeId.desc())
                .limit(size)
                .fetch();

        List<String> placeIds = places.stream()
                .map(Place::getPlaceId)
                .toList();

        Map<String, Double> ratingMap = queryFactory
                .select(review.place.placeId, review.rating.avg())
                .from(review)
                .where(review.place.placeId.in(placeIds))
                .groupBy(review.place.placeId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(review.place.placeId),
                        tuple -> Optional.ofNullable(tuple.get(review.rating.avg())).orElse(0.0)
                ));

        List<String> bookmarkedIds = (email != null && !email.isBlank())
                ? queryFactory.select(bookmark.place.placeId)
                .from(bookmark)
                .join(bookmark.member, member)
                .where(member.email.eq(email))
                .fetch()
                : List.of();

        return places.stream()
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
                        bookmarkedIds.contains(p.getPlaceId()),
                        ratingMap.getOrDefault(p.getPlaceId(), 0.0)
                ))
                .toList();
    }


    @Override
    public List<PlaceSearchResDto> searchByKeywordWithCursor(String keyword, String cursor, int size) {
        QPlace place = QPlace.place;

        BooleanBuilder builder = new BooleanBuilder()
                .and(
                        place.menu1.containsIgnoreCase(keyword)
                                .or(place.menu2.containsIgnoreCase(keyword))
                                .or(place.businessName.containsIgnoreCase(keyword))
                );

        if (cursor != null) {
            builder.and(place.placeId.gt(cursor));
        }

        return queryFactory
                .select(Projections.constructor(PlaceSearchResDto.class,
                        place.businessName,
                        place.placeId
                ))
                .from(place)
                .where(builder)
                .orderBy(place.placeId.asc())
                .limit(size + 1)
                .fetch();
    }
}
