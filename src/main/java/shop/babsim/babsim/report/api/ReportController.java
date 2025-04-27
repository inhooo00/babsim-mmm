package shop.babsim.babsim.report.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.report.api.dto.request.ReportReqDto;
import shop.babsim.babsim.report.api.dto.response.ReportListResDto;
import shop.babsim.babsim.report.api.dto.response.ReportResDto;
import shop.babsim.babsim.report.application.ReportService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController implements ReportDocs{

    private final ReportService reportService;

    @PostMapping()
    public RspTemplate<ReportResDto> saveReport(
            @CurrentUserEmail String email,
            @RequestBody ReportReqDto reportReqDto
    ) {
        return new RspTemplate<>(HttpStatus.OK, "신고 작성", reportService.saveReport(email, reportReqDto));
    }

    @GetMapping("/my-reports")
    public RspTemplate<ReportListResDto> findMyReports(
            @CurrentUserEmail String email,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return new RspTemplate<>(HttpStatus.OK, "내가 작성한 신고 리스트", reportService.findReportByEmail(email,
                PageRequest.of(page, size)));
    }
}
