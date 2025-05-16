package shop.babsim.babsim.report.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.babsim.babsim.global.entity.BaseEntity;
import shop.babsim.babsim.global.entity.Status;
import shop.babsim.babsim.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor
public class Report extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private Status status;

    private String businessName; // 가게 이름

    private String address; // 주소

    private String placeId; // 가게 아이디

    private String menu;

    private String price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Report(Member member, String businessName, String address, String placeId, String menu, String price) {
        this.member = member;
        this.status = Status.ACTIVE;
        this.businessName = businessName;
        this.address = address;
        this.placeId = placeId;
        this.menu = menu;
        this.price = price;
    }
}
