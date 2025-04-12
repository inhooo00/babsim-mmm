package shop.babsim.babsim.report.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.report.domain.Report;

@Builder
public record ReportResDto(
        Long memberId,
        String businessName,
        String address,
        String placeId,
        String menu,
        String price
) {
    public static ReportResDto from(Report report) {
        return ReportResDto.builder()
                .memberId(report.getMember().getId())
                .businessName(report.getBusinessName())
                .address(report.getAddress())
                .placeId(report.getPlaceId())
                .menu(report.getMenu())
                .price(report.getPrice())
                .build();
    }
}
