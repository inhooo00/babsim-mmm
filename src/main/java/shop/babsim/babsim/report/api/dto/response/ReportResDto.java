package shop.babsim.babsim.report.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.report.domain.Report;

@Builder
public record ReportResDto(
        Long memberId,
        String businessName,
        String address,
        String menu,
        String price
) {
    public static ReportResDto from(Report report) {
        return ReportResDto.builder()
                .memberId(report.getMember() != null ? report.getMember().getId() : null)
                .businessName(report.getBusinessName())
                .address(report.getAddress())
                .menu(report.getMenu())
                .price(report.getPrice())
                .build();
    }
}
