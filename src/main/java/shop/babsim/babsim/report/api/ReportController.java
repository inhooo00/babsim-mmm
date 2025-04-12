package shop.babsim.babsim.report.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.babsim.babsim.global.annotation.CurrentUserEmail;
import shop.babsim.babsim.global.template.RspTemplate;
import shop.babsim.babsim.report.api.dto.request.ReportReqDto;
import shop.babsim.babsim.report.api.dto.response.ReportResDto;
import shop.babsim.babsim.report.application.ReportService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping()
    public RspTemplate<ReportResDto> saveReport(
            @CurrentUserEmail String email,
            @RequestBody ReportReqDto reportReqDto
    ) {
        return new RspTemplate<>(HttpStatus.OK, "신고 작성", reportService.saveReport(email, reportReqDto));
    }
}
