package shop.babsim.babsim.report.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.global.dto.PageInfoResDto;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.report.api.cursordto.ReportCursorResDto;
import shop.babsim.babsim.report.api.dto.request.ReportReqDto;
import shop.babsim.babsim.report.api.dto.response.ReportListResDto;
import shop.babsim.babsim.report.api.dto.response.ReportResDto;
import shop.babsim.babsim.report.discord.application.DiscordWebhookUtil;
import shop.babsim.babsim.report.discord.domain.DiscordMessage;
import shop.babsim.babsim.report.domain.Report;
import shop.babsim.babsim.report.domain.repository.ReportRepository;
import shop.babsim.babsim.review.api.dto.response.ReviewListResDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final DiscordWebhookUtil discordWebhookUtil;

    @Transactional
    public ReportResDto saveReport(String email, ReportReqDto reportReqDto) {
        boolean isAnonymous = (email == null || email.isBlank() || email.equalsIgnoreCase("익명"));

        Member member = null;
        if (!isAnonymous) {
            member = memberRepository.findByEmail(email)
                    .orElseThrow(MemberNotFoundException::new);
        }

        Report report = ReportReqDto.toEntity(member, reportReqDto);
        reportRepository.save(report);

        String message = """
            신고 접수됨!
            ▪ 장소명: %s
            ▪ 주소: %s
            ▪ 메뉴: %s
            ▪ 가격: %s
            """.formatted(
                safe(report.getBusinessName()),
                safe(report.getAddress()),
                safe(report.getMenu()),
                safe(report.getPrice())
        );

        discordWebhookUtil.sendDiscordMessage(message, member != null ? member.getId() : null);

        return ReportResDto.from(report);
    }

    private String safe(String value) {
        return value != null ? value : "(없음)";
    }

    public ReportListResDto findReportByEmail(String email, Pageable pageable) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        Page<ReportResDto> reports = reportRepository.findAllByMemberId(member.getId(), pageable);

        return ReportListResDto.of(
                reports.getContent(),
                PageInfoResDto.from(reports)
        );
    }

    public ReportCursorResDto findReportByEmailWithCursor(String email, Long cursorId, int size) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        List<Report> raw = reportRepository.findAllByMemberIdWithCursor(
                member.getId(), cursorId, PageRequest.of(0, size + 1));

        boolean hasNext = raw.size() > size;
        List<Report> trimmed = hasNext ? raw.subList(0, size) : raw;

        List<ReportResDto> data = trimmed.stream()
                .map(ReportResDto::from)
                .toList();

        Long nextCursor = hasNext ? trimmed.get(trimmed.size() - 1).getId() : null;

        return ReportCursorResDto.of(data, nextCursor);
    }

}
