package shop.babsim.babsim.review.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.review.api.cursordto.ReviewCursorResDto;
import shop.babsim.babsim.review.api.dto.request.ReviewSaveReqDto;
import shop.babsim.babsim.review.api.dto.request.ReviewSaveWithBase64Dto;
import shop.babsim.babsim.review.api.dto.response.ReviewInfoResDto;
import shop.babsim.babsim.review.api.dto.response.ReviewListResDto;
import shop.babsim.babsim.review.api.dto.response.ReviewSaveInfoResDto;
import shop.babsim.babsim.review.application.ReviewService;
import shop.babsim.babsim.review.s3.application.AwsS3Service;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController implements ReviewDocs {

    private final ReviewService reviewService;
    private final AwsS3Service awsS3Service;

    @PostMapping
    public RspTemplate<ReviewSaveInfoResDto> save(@CurrentUserEmail String email,
                                                  @RequestPart("reviewSaveReqDto") String reviewSaveReqDtoJson,
                                                  @RequestPart(value = "reviewImage") List<MultipartFile> reviewImage) {
        List<String> imageUrls = awsS3Service.uploadFile(reviewImage);
        ObjectMapper objectMapper = new ObjectMapper();
        ReviewSaveReqDto reviewSaveReqDto;

        try {
            reviewSaveReqDto = objectMapper.readValue(reviewSaveReqDtoJson, ReviewSaveReqDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 오류: " + e.getMessage());
        }

        return new RspTemplate<>(HttpStatus.CREATED, "리뷰 생성", reviewService.save(email, reviewSaveReqDto, imageUrls));
    }

    @GetMapping("/{reviewId}")
    public RspTemplate<ReviewInfoResDto> getReviewDetail(@PathVariable(name = "reviewId") Long reviewId) {
        return new RspTemplate<>(HttpStatus.OK, "리뷰 개별 조회", reviewService.findById(reviewId));
    }

    @GetMapping("/all/{placeId}")
    public RspTemplate<ReviewListResDto> getReviewList(@CurrentUserEmail String email,
                                                       @PathVariable(name = "placeId") String placeId,
                                                       @RequestParam(name = "page", defaultValue = "0") int page,
                                                       @RequestParam(name = "size", defaultValue = "10") int size) {
        return new RspTemplate<>(HttpStatus.OK, "리뷰 전체 조회",
                reviewService.findByPlaceIdExcludingBlocked(email, placeId,
                        PageRequest.of(page, size)));
    }

    @GetMapping("/my-reviews")
    public RspTemplate<ReviewListResDto> getMyReviewList(@CurrentUserEmail String email,
                                                         @RequestParam(name = "page", defaultValue = "0") int page,
                                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        return new RspTemplate<>(HttpStatus.OK, "내 리뷰 조회",
                reviewService.findByEmail(email, PageRequest.of(page, size)));
    }

    @GetMapping("/all/{placeId}/cursor")
    public RspTemplate<ReviewCursorResDto> getReviewListWithCursor(
            @CurrentUserEmail String email,
            @PathVariable(name = "placeId") String placeId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new RspTemplate<>(HttpStatus.OK, "리뷰 커서 기반 전체 조회",
                reviewService.findByPlaceIdWithCursor(email, placeId, cursorId, size));
    }

    @GetMapping("/my-reviews/cursor")
    public RspTemplate<ReviewCursorResDto> getMyReviewListWithCursor(
            @CurrentUserEmail String email,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new RspTemplate<>(HttpStatus.OK, "커서 기반 내 리뷰 조회",
                reviewService.findByEmailWithCursor(email, cursorId, size));
    }

    @PostMapping("/base64")
    public RspTemplate<ReviewSaveInfoResDto> saveWithBase64(@CurrentUserEmail String email,
                                                            @RequestBody ReviewSaveWithBase64Dto request) {
        List<String> imageUrls = awsS3Service.uploadBase64Images(request.reviewImages());

        return new RspTemplate<>(HttpStatus.CREATED, "리뷰 생성",
                reviewService.Base64Save(email, request, imageUrls));
    }
}
