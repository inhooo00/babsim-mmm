package shop.babsim.babsim.review.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.global.dto.PageInfoResDto;
import shop.babsim.babsim.heart.domain.repository.HeartRepository;
import shop.babsim.babsim.member.block.domain.repository.BlockRepository;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.place.domain.repository.PlaceRepository;
import shop.babsim.babsim.place.exception.PlaceNotFoundException;
import shop.babsim.babsim.review.api.cursordto.ReviewCursorResDto;
import shop.babsim.babsim.review.api.dto.request.ReviewSaveReqDto;
import shop.babsim.babsim.review.api.dto.request.ReviewSaveWithBase64Dto;
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
    private final HeartRepository heartRepository;
    @Transactional
    public ReviewSaveInfoResDto save(String email, ReviewSaveReqDto reviewSaveReqDto, List<String> imageUrls) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Place place = placeRepository.findByPlaceId(reviewSaveReqDto.placeId())
                .orElseThrow(PlaceNotFoundException::new);
        Review review = reviewRepository.save(reviewSaveReqDto.toEntity(member, place, imageUrls));

        return ReviewSaveInfoResDto.of(review, member.getId());
    }

    public ReviewInfoResDto findById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        String imageUrl = awsS3Service.getFileUrls(review.getFeedImage());

        return ReviewInfoResDto.of(review, imageUrl);
    }

    public ReviewListResDto findByPlaceIdExcludingBlocked(String email, String placeId, Pageable pageable) {
        List<Long> blockedIds = (email != null && !email.isBlank())
                ? blockRepository.findBlockedMemberIdsByEmail(email)
                : List.of();

        Page<ReviewInfoResDto> reviews = reviewRepository.findAllByPlaceIdExcludingBlocked(placeId, blockedIds,
                pageable);

        return ReviewListResDto.of(reviews.getContent(), PageInfoResDto.from(reviews));
    }

    public ReviewListResDto findByEmail(String email, Pageable pageable) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Page<ReviewInfoResDto> reviews = reviewRepository.findAllByMemberId(member.getId(), pageable);

        return ReviewListResDto.of(
                reviews.getContent(),
                PageInfoResDto.from(reviews)
        );
    }

    public ReviewCursorResDto findByPlaceIdWithCursor(String email, String placeId, Long cursorId, int size) {
        List<Review> raw = reviewRepository.findByPlaceIdExcludingBlockedWithCursor(
                email, placeId, cursorId, PageRequest.of(0, size + 1));

        boolean hasNext = raw.size() > size;
        List<Review> trimmed = hasNext ? raw.subList(0, size) : raw;

        List<ReviewInfoResDto> data = trimmed.stream()
                .map(review -> {
                    boolean isLike = heartRepository.existsByMemberEmailAndReviewId(email, review.getId());
                    return ReviewInfoResDto.of(review, memberRepository.getReviewCountByEmail(review.getMember().getEmail()), isLike);
                })
                .toList();

        Long nextCursor = hasNext ? trimmed.get(trimmed.size() - 1).getId() : null;

        return ReviewCursorResDto.of(data, nextCursor);
    }

    public ReviewCursorResDto findByEmailWithCursor(String email, Long cursorId, int size) {
        List<Review> raw = reviewRepository.findByEmailWithCursor(
                email, cursorId, PageRequest.of(0, size + 1));

        boolean hasNext = raw.size() > size;
        List<Review> trimmed = hasNext ? raw.subList(0, size) : raw;

        List<ReviewInfoResDto> data = trimmed.stream()
                .map(review -> {
                    boolean isLike = heartRepository.existsByMemberEmailAndReviewId(email, review.getId());
                    return ReviewInfoResDto.of(review, memberRepository.getReviewCountByEmail(email), isLike);
                })
                .toList();

        Long nextCursor = hasNext ? trimmed.get(trimmed.size() - 1).getId() : null;

        return ReviewCursorResDto.of(data, nextCursor);
    }

    @Transactional
    public ReviewSaveInfoResDto Base64Save(String email, ReviewSaveWithBase64Dto reviewSaveWithBase64Dto, List<String> imageUrls) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        Place place = placeRepository.findByPlaceId(reviewSaveWithBase64Dto.placeId())
                .orElseThrow(PlaceNotFoundException::new);

        Review review = reviewRepository.save(reviewSaveWithBase64Dto
                .toEntity(member, place, imageUrls));

        return ReviewSaveInfoResDto.of(review, member.getId());
    }

}
