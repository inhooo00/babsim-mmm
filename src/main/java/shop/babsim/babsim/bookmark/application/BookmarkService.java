package shop.babsim.babsim.bookmark.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.bookmark.api.cursordto.BookmarkCursorResDto;
import shop.babsim.babsim.bookmark.api.dto.response.BookmarkResDto;
import shop.babsim.babsim.bookmark.api.dto.response.BookmarkResListDto;
import shop.babsim.babsim.bookmark.domain.Bookmark;
import shop.babsim.babsim.bookmark.domain.repository.BookmarkRepository;
import shop.babsim.babsim.global.dto.PageInfoResDto;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.review.domain.repository.ReviewRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createOrDeleteBookmark(String email, String placeId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        bookmarkRepository.createOrDeleteBookmark(member, placeId);
    }

    // 나의 찜 리스트 조회
    public BookmarkResListDto findMyBookmarkList(String email, Pageable pageable) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        Page<Bookmark> bookmarks = bookmarkRepository.findMyBookmarksByMember(member, pageable);

        List<BookmarkResDto> bookmarkResDtos = bookmarks.stream()
                .map(bookmark -> {
                    Double averageRating = findAverageRatingByPlaceId(bookmark.getPlace().getPlaceId());
                    return BookmarkResDto.from(bookmark, averageRating);
                })
                .toList();

        return BookmarkResListDto.of(bookmarkResDtos, PageInfoResDto.from(bookmarks));
    }

    // 평균 레이팅
    private Double findAverageRatingByPlaceId(String placeId) {
        return reviewRepository.getRatingAvgByPlaceId(placeId);
    }

    // 찜 개수 기준 맛집 추천
    public BookmarkResListDto findTopPlacesByBookmarkCount(Pageable pageable) {
        Page<Place> topPlaces = bookmarkRepository.findTopPlacesByBookmarkCount(pageable);

        List<BookmarkResDto> bookmarkResDtos = topPlaces.stream()
                .map(bookmark -> {
                    Double averageRating = findAverageRatingByPlaceId(bookmark.getPlaceId());
                    return BookmarkResDto.from(bookmark, averageRating);
                })
                .toList();

        return BookmarkResListDto.of(bookmarkResDtos, PageInfoResDto.from(topPlaces));
    }

    public BookmarkCursorResDto findMyBookmarksWithCursor(String email, Long cursorId, int size) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        List<Bookmark> rawResults = bookmarkRepository.findMyBookmarksWithCursor(member, cursorId, PageRequest.of(0, size + 1));
        boolean hasNext = rawResults.size() > size;
        List<Bookmark> trimmed = hasNext ? rawResults.subList(0, size) : rawResults;

        List<BookmarkResDto> data = trimmed.stream()
                .map(b -> BookmarkResDto.from(b, findAverageRatingByPlaceId(b.getPlace().getPlaceId())))
                .toList();

        Long nextCursor = hasNext ? trimmed.get(trimmed.size() - 1).getId() : null;

        return BookmarkCursorResDto.of(data, nextCursor);
    }
}
