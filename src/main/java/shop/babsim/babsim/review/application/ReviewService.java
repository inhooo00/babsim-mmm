package shop.babsim.babsim.review.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.global.dto.PageInfoResDto;
import shop.babsim.babsim.member.block.domain.repository.BlockRepository;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.place.domain.repository.PlaceRepository;
import shop.babsim.babsim.place.exception.PlaceNotFoundException;
import shop.babsim.babsim.review.api.dto.request.ReviewSaveReqDto;
import shop.babsim.babsim.review.api.dto.response.ReviewInfoResDto;
import shop.babsim.babsim.review.api.dto.response.ReviewListResDto;
import shop.babsim.babsim.review.api.dto.response.ReviewSaveInfoResDto;
import shop.babsim.babsim.review.domain.Review;
import shop.babsim.babsim.review.domain.repository.ReviewRepository;
import shop.babsim.babsim.review.exception.ReviewNotFoundException;
import shop.babsim.babsim.review.s3.application.AwsS3Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final AwsS3Service awsS3Service;
    private final BlockRepository blockRepository;

    // 리뷰 생성
    @Transactional
    public ReviewSaveInfoResDto save(String email, ReviewSaveReqDto reviewSaveReqDto, List<String> imageUrls) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Place place = placeRepository.findByPlaceId(reviewSaveReqDto.placeId())
                .orElseThrow(PlaceNotFoundException::new);
        Review review = reviewRepository.save(reviewSaveReqDto.toEntity(member, place, imageUrls));

        return ReviewSaveInfoResDto.of(review, member.getId());
    }

    // 리뷰 개별 조회
    public ReviewInfoResDto findById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        String imageUrl = awsS3Service.getFileUrls(review.getFeedImage());

        return ReviewInfoResDto.of(review, imageUrl);
    }

    // 장소 id로 리뷰 리스트 조회
    public ReviewListResDto findByPlaceIdExcludingBlocked(String email, String placeId, Pageable pageable) {
        List<Long> blockedIds = (email != null && !email.isBlank())
                ? blockRepository.findBlockedMemberIdsByEmail(email)
                : List.of();

        Page<ReviewInfoResDto> reviews = reviewRepository.findAllByPlaceIdExcludingBlocked(placeId, blockedIds,
                pageable);

        return ReviewListResDto.of(reviews.getContent(), PageInfoResDto.from(reviews));
    }


    // 이메일로 리뷰 리스트 조회
    public ReviewListResDto findByEmail(String email, Pageable pageable) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Page<ReviewInfoResDto> reviews = reviewRepository.findAllByMemberId(member.getId(), pageable);

        return ReviewListResDto.of(
                reviews.getContent(),
                PageInfoResDto.from(reviews)
        );
    }
}
