package shop.babsim.babsim.report.api.dto.request;

import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.report.domain.Report;

public record ReportReqDto(
        Long memberId,
        String businessName,
        String address,
        String menu,
        String price
) {
    public static Report toEntity(Member member,ReportReqDto reportReqDto) {
        return Report.builder()
                .member(member)
                .businessName(reportReqDto.businessName)
                .address(reportReqDto.address)
                .menu(reportReqDto.menu)
                .price(reportReqDto.price)
                .build();
    }
}
