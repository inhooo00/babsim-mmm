package shop.babsim.babsim.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shop.babsim.babsim.global.dto.PageInfoResDto;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;
import shop.babsim.babsim.report.api.dto.request.ReportReqDto;
import shop.babsim.babsim.report.api.dto.response.ReportListResDto;
import shop.babsim.babsim.report.api.dto.response.ReportResDto;
import shop.babsim.babsim.report.application.ReportService;
import shop.babsim.babsim.report.discord.application.DiscordWebhookUtil;
import shop.babsim.babsim.report.domain.Report;
import shop.babsim.babsim.report.domain.repository.ReportRepository;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private DiscordWebhookUtil discordWebhookUtil;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("신고 저장 성공")
    void saveReport_success() {
        // given
        String email = "user@example.com";
        Long memberId = 1L;

        Member member = mock(Member.class);
        when(member.getId()).thenReturn(memberId);
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        ReportReqDto reportReqDto = new ReportReqDto(
                memberId,
                "테스트 장소",
                "주소요",
                "정돈 먹고 싶다",
                "₩17000"
        );

        Report report = ReportReqDto.toEntity(member, reportReqDto);
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        // when
        ReportResDto result = reportService.saveReport(email, reportReqDto);

        // then
        assertThat(result).isNotNull();
        verify(reportRepository).save(any(Report.class));
        verify(discordWebhookUtil).sendDiscordMessage(anyString(), eq(memberId));
    }



    @Test
    @DisplayName("신고 저장 실패 - 회원 없음")
    void saveReport_fail_memberNotFound() {
        String email = "inho@example.com";
        ReportReqDto dto = mock(ReportReqDto.class);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.saveReport(email, dto))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("이메일로 신고 목록 조회 성공")
    void findReportByEmail_success() {
        String email = "inho@example.com";
        Member member = mock(Member.class);
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReportResDto> fakePage = new PageImpl<>(Collections.emptyList());

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(member.getId()).thenReturn(memberId);
        when(reportRepository.findAllByMemberId(memberId, pageable)).thenReturn(fakePage);

        ReportListResDto result = reportService.findReportByEmail(email, pageable);

        assertThat(result).isNotNull();
        assertThat(result.pageInfoResDto()).isInstanceOf(PageInfoResDto.class);
        verify(reportRepository).findAllByMemberId(memberId, pageable);
    }

    @Test
    @DisplayName("신고 목록 조회 실패 - 회원 없음")
    void findReportByEmail_fail_memberNotFound() {
        String email = "inho@example.com";
        Pageable pageable = PageRequest.of(0, 10);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.findReportByEmail(email, pageable))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
