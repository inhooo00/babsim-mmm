package shop.babsim.babsim.bookmark.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.babsim.babsim.bookmark.domain.Bookmark;
import shop.babsim.babsim.member.domain.Member;

public interface BookmarkCustomRepository {
    void createOrDeleteBookmark(Member member, String placeId);

    Page<Bookmark> findMyBookmarksByMember(Member member, Pageable pageable);

}
