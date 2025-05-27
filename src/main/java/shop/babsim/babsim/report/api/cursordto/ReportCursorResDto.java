package shop.babsim.babsim.report.api.cursordto;

import java.util.List;
import shop.babsim.babsim.report.api.dto.response.ReportResDto;

public record ReportCursorResDto(
        List<ReportResDto> data,
        Long nextCursor,
        boolean hasNext
) {
    public static ReportCursorResDto of(List<ReportResDto> data, Long nextCursor) {
        return new ReportCursorResDto(data, nextCursor, nextCursor != null);
    }
}
