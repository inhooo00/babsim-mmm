package shop.babsim.babsim.complaint.api.dto.request;

import shop.babsim.babsim.complaint.domain.ComplaintType;

public record ReviewReportReqDto(Long reviewId, ComplaintType type, String reason) {
}
