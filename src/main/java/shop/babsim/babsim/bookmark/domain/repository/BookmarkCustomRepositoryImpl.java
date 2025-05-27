package shop.babsim.babsim.bookmark.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.bookmark.domain.Bookmark;
import shop.babsim.babsim.bookmark.domain.QBookmark;
import shop.babsim.babsim.member.domain.QMember;
import shop.babsim.babsim.place.domain.QPlace;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.place.exception.PlaceNotFoundException;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkCustomRepositoryImpl implements BookmarkCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    private static final QBookmark bookmark = QBookmark.bookmark;
    private static final QPlace place = QPlace.place;

    @Override
    @Transactional
    public void createOrDeleteBookmark(Member member, String placeId) {

        boolean exists = queryFactory
                .selectOne()
                .from(bookmark)
                .where(bookmark.member.eq(member).and(bookmark.place.placeId.eq(placeId)))
                .fetchFirst() != null;

        Place targetPlace = queryFactory
                .selectFrom(place)
                .where(place.placeId.eq(placeId))
                .fetchOne();

        if (targetPlace == null) {
            throw new PlaceNotFoundException();
        }

        if (!exists) {
            Bookmark newBookmark = new Bookmark(member, targetPlace);
            entityManager.persist(newBookmark);

        } else {
            queryFactory
                    .delete(bookmark)
                    .where(bookmark.member.eq(member).and(bookmark.place.placeId.eq(placeId)))
                    .execute();
        }
    }

    @Override
    public Page<Bookmark> findMyBookmarksByMember(Member member, Pageable pageable) {
        List<Bookmark> content = queryFactory
                .select(bookmark)
                .from(bookmark)
                .where(eqMember(member))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(bookmark.count())
                .from(bookmark)
                .where(eqMember(member));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression eqMember(Member member) {
        return member != null ? bookmark.member.eq(member) : null;
    }

    public boolean isBookmarked(String email, String placeId) {
        QBookmark bookmark = QBookmark.bookmark;
        QMember member = QMember.member;

        Integer count = queryFactory
                .selectOne()
                .from(bookmark)
                .join(bookmark.member, member)
                .where(
                        member.email.eq(email),
                        bookmark.place.placeId.eq(placeId)
                )
                .fetchFirst();

        return count != null;
    }

}
