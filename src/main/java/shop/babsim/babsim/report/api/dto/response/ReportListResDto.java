package shop.babsim.babsim.report.api.dto.response;

import java.util.List;
import lombok.Builder;
import shop.babsim.babsim.global.dto.PageInfoResDto;

@Builder
public record ReportListResDto(
        List<ReportResDto> reportResDtos,
        PageInfoResDto pageInfoResDto
) {
    public static ReportListResDto of(List<ReportResDto> reviewListResDtos, PageInfoResDto pageInfoResDto) {
        return ReportListResDto.builder()
                .reportResDtos(reviewListResDtos)
                .pageInfoResDto(pageInfoResDto)
                .build();
    }
}
