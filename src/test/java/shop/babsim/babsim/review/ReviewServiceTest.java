package shop.babsim.babsim.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shop.babsim.babsim.global.dto.PageInfoResDto;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.place.domain.repository.PlaceRepository;
import shop.babsim.babsim.place.exception.PlaceNotFoundException;
import shop.babsim.babsim.review.api.dto.request.ReviewSaveReqDto;
import shop.babsim.babsim.review.api.dto.response.ReviewInfoResDto;
import shop.babsim.babsim.review.api.dto.response.ReviewSaveInfoResDto;
import shop.babsim.babsim.review.application.ReviewService;
import shop.babsim.babsim.review.domain.Review;
import shop.babsim.babsim.review.domain.repository.ReviewRepository;
import shop.babsim.babsim.review.exception.ReviewNotFoundException;
import shop.babsim.babsim.review.s3.application.AwsS3Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AwsS3Service awsS3Service;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("리뷰 저장 성공")
    void save_success() {
        String email = "test@naver.com";
        List<String> imageUrls = List.of("url1", "url2");

        Member member = mock(Member.class);
        Place place = mock(Place.class);
        Review review = mock(Review.class);

        ReviewSaveReqDto dto = mock(ReviewSaveReqDto.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(placeRepository.findByPlaceId(dto.placeId())).thenReturn(Optional.of(place));
        when(dto.toEntity(member, place, imageUrls)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        when(member.getId()).thenReturn(1L);

        ReviewSaveInfoResDto result = reviewService.save(email, dto, imageUrls);

        assertThat(result).isNotNull();
        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("리뷰 저장 실패 - 회원 없음")
    void save_fail_memberNotFound() {
        String email = "notfound@naver.com";
        ReviewSaveReqDto dto = mock(ReviewSaveReqDto.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.save(email, dto, List.of()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("리뷰 저장 실패 - 장소 없음")
    void save_fail_placeNotFound() {
        String email = "test@naver.com";
        ReviewSaveReqDto dto = mock(ReviewSaveReqDto.class);
        Member member = mock(Member.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(placeRepository.findByPlaceId(dto.placeId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.save(email, dto, List.of()))
                .isInstanceOf(PlaceNotFoundException.class);
    }

    @Test
    @DisplayName("리뷰 ID로 조회 성공")
    void findById_success() {
        Long reviewId = 1L;
        Member member = mock(Member.class);

        Review review = mock(Review.class);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(review.getFeedImage()).thenReturn("imageKey");
        when(review.getMember()).thenReturn(member);
        when(awsS3Service.getFileUrls("imageKey")).thenReturn("https://s3.com/image.jpg");

        ReviewInfoResDto result = reviewService.findById(reviewId);

        assertThat(result).isNotNull();
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    @DisplayName("리뷰 ID로 조회 실패 - 존재하지 않음")
    void findById_fail_notFound() {
        Long reviewId = 1L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.findById(reviewId))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("장소 ID로 리뷰 리스트 조회 성공")
    void findByPlaceId_success() {
        String placeId = "abc123";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewInfoResDto> fakePage = new PageImpl<>(Collections.emptyList());

        when(reviewRepository.findAllByPlaceId(placeId, pageable)).thenReturn(fakePage);

        var result = reviewService.findByPlaceId(placeId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.pageInfoResDto()).isInstanceOf(PageInfoResDto.class);
        verify(reviewRepository).findAllByPlaceId(placeId, pageable);
    }

    @Test
    @DisplayName("이메일로 리뷰 리스트 조회 성공")
    void findByEmail_success() {
        Long memberId = 1L;
        String email = "inho@naver.com";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewInfoResDto> fakePage = new PageImpl<>(Collections.emptyList());

        Member member = mock(Member.class);

        when(member.getId()).thenReturn(memberId);
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(reviewRepository.findAllByMemberId(memberId, pageable)).thenReturn(fakePage);

        var result = reviewService.findByEmail(email, pageable);

        assertThat(result).isNotNull();
        assertThat(result.pageInfoResDto()).isInstanceOf(PageInfoResDto.class);
        verify(reviewRepository).findAllByMemberId(memberId, pageable);
    }

}
