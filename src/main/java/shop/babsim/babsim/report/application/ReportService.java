package shop.babsim.babsim.report.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.report.api.dto.request.ReportReqDto;
import shop.babsim.babsim.report.api.dto.response.ReportResDto;
import shop.babsim.babsim.report.discord.application.DiscordWebhookUtil;
import shop.babsim.babsim.report.discord.domain.DiscordMessage;
import shop.babsim.babsim.report.domain.Report;
import shop.babsim.babsim.report.domain.repository.ReportRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final DiscordWebhookUtil discordWebhookUtil;

    @Transactional
    public ReportResDto saveReport(String email, ReportReqDto reportReqDto) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Report report = ReportReqDto.toEntity(member, reportReqDto);

        reportRepository.save(report);
        discordWebhookUtil.sendDiscordMessage(DiscordMessage.REPORT.getMessage(), member.getId());

        return ReportResDto.from(report);
    }
}
