package shop.babsim.babsim.bookmark.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.babsim.babsim.bookmark.api.dto.response.BookmarkResListDto;
import shop.babsim.babsim.bookmark.application.BookmarkService;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{placeId}")
    public RspTemplate<Void> createOrDeleteBookmark(
            @CurrentUserEmail String email,
            @PathVariable String placeId) {

        bookmarkService.createOrDeleteBookmark(email, placeId);

        return new RspTemplate<>(HttpStatus.OK, "찜 등록/삭제 성공");
    }

    @GetMapping()
    public RspTemplate<BookmarkResListDto> findMyBookmarkList(
            @CurrentUserEmail String email,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return new RspTemplate<>(HttpStatus.OK, "찜 리스트 조회 성공",
                bookmarkService.findMyBookmarkList(email, PageRequest.of(page, size)));
    }

    @GetMapping("/top")
    public RspTemplate<BookmarkResListDto> findTopPlacesByBookmarkCount(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return new RspTemplate<>(HttpStatus.OK, "찜 개수 기준 맛집 추천 조회 성공",
                bookmarkService.findTopPlacesByBookmarkCount(PageRequest.of(page, size)));
    }
}