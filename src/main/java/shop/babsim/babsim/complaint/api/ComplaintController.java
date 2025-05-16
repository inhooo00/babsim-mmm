package shop.babsim.babsim.complaint.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import shop.babsim.babsim.complaint.api.dto.request.ReviewReportReqDto;
import shop.babsim.babsim.complaint.application.ComplaintService;
import shop.babsim.babsim.complaint.domain.ComplaintStatus;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController implements ComplaintDocs{

    private final ComplaintService complaintService;

    @PostMapping("/review")
    public RspTemplate<Void> reportReview(
            @CurrentUserEmail String email,
            @RequestBody ReviewReportReqDto reviewReportReqDto) {

        complaintService.reportReview(
                email,
                reviewReportReqDto
        );
        return new RspTemplate<>(HttpStatus.OK, "신고가 접수되었습니다.");
    }

    @PatchMapping("/{complaintId}")
    public RspTemplate<Void> updateComplaintStatus(
            @PathVariable Long complaintId,
            @RequestParam("status") ComplaintStatus status) {

        complaintService.resolveComplaint(complaintId, status);
        return new RspTemplate<>(HttpStatus.OK, "신고 상태가 변경되었습니다.");
    }
}
