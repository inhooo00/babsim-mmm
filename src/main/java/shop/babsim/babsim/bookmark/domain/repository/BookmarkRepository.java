package shop.babsim.babsim.bookmark.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.babsim.babsim.bookmark.domain.Bookmark;
import shop.babsim.babsim.place.domain.Place;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkCustomRepository {

    @Query("SELECT b.place, COUNT(b) AS bookmarkCount FROM Bookmark b " +
            "GROUP BY b.place " +
            "ORDER BY bookmarkCount DESC")
    Page<Place> findTopPlacesByBookmarkCount(Pageable pageable);
}
