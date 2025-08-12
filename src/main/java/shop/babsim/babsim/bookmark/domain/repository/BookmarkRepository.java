package shop.babsim.babsim.bookmark.domain.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.babsim.babsim.bookmark.api.cursordto.BookmarkCursorResDto;
import shop.babsim.babsim.bookmark.domain.Bookmark;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.place.domain.Place;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkCustomRepository {

    @Query("""
    SELECT p, COUNT(b) AS bookmarkCount
    FROM Place p
    LEFT JOIN Bookmark b ON b.place = p
    GROUP BY p
    ORDER BY bookmarkCount DESC
""")
    Page<Place> findTopPlacesByBookmarkCount(Pageable pageable);

    @Query("""
            SELECT b FROM Bookmark b
            WHERE b.member = :member
              AND (:cursorId IS NULL OR b.id < :cursorId)
            ORDER BY b.id DESC
            """)
    List<Bookmark> findMyBookmarksWithCursor(
            @Param("member") Member member,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
