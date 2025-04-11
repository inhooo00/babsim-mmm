package shop.babsim.babsim.bookmark;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shop.babsim.babsim.bookmark.api.dto.response.BookmarkResDto;
import shop.babsim.babsim.bookmark.api.dto.response.BookmarkResListDto;
import shop.babsim.babsim.bookmark.application.BookmarkService;
import shop.babsim.babsim.bookmark.domain.Bookmark;
import shop.babsim.babsim.bookmark.domain.repository.BookmarkRepository;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.review.domain.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("북마크 생성 또는 삭제 성공")
    void createOrDeleteBookmark_success() {
        String email = "test@example.com";
        String placeId = "place123";
        Member member = mock(Member.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        bookmarkService.createOrDeleteBookmark(email, placeId);

        verify(memberRepository).findByEmail(email);
        verify(bookmarkRepository).createOrDeleteBookmark(member, placeId);
    }

    @Test
    @DisplayName("북마크 생성 또는 삭제 실패 - 회원 없음")
    void createOrDeleteBookmark_fail_memberNotFound() {
        String email = "noone@example.com";
        String placeId = "place123";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookmarkService.createOrDeleteBookmark(email, placeId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("나의 찜 리스트 조회 성공")
    void findMyBookmarkList_success() {
        String email = "user@example.com";
        Member member = mock(Member.class);
        Pageable pageable = PageRequest.of(0, 10);
        Place place = mock(Place.class);
        when(place.getPlaceId()).thenReturn("place123");

        Bookmark bookmark = mock(Bookmark.class);
        when(bookmark.getPlace()).thenReturn(place);
        Page<Bookmark> page = new PageImpl<>(List.of(bookmark));

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(bookmarkRepository.findMyBookmarksByMember(member, pageable)).thenReturn(page);
        when(reviewRepository.getRatingAvgByPlaceId("place123")).thenReturn(4.5);

        BookmarkResListDto result = bookmarkService.findMyBookmarkList(email, pageable);

        assertThat(result).isNotNull();
        verify(memberRepository).findByEmail(email);
        verify(bookmarkRepository).findMyBookmarksByMember(member, pageable);
        verify(reviewRepository).getRatingAvgByPlaceId("place123");
    }

    @Test
    @DisplayName("찜 개수 기준 맛집 추천 성공")
    void findTopPlacesByBookmarkCount_success() {
        Pageable pageable = PageRequest.of(0, 5);
        Place place = mock(Place.class);
        when(place.getPlaceId()).thenReturn("place456");
        Page<Place> page = new PageImpl<>(List.of(place));

        when(bookmarkRepository.findTopPlacesByBookmarkCount(pageable)).thenReturn(page);
        when(reviewRepository.getRatingAvgByPlaceId("place456")).thenReturn(3.8);

        BookmarkResListDto result = bookmarkService.findTopPlacesByBookmarkCount(pageable);

        assertThat(result).isNotNull();
        verify(bookmarkRepository).findTopPlacesByBookmarkCount(pageable);
        verify(reviewRepository).getRatingAvgByPlaceId("place456");
    }
}
