package shop.babsim.babsim.complaint.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.babsim.babsim.global.entity.BaseEntity;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.review.domain.Review;

@Entity
@Getter
@NoArgsConstructor
public class Complaint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Enumerated(EnumType.STRING)
    private ComplaintType type;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    private String reason;

    public Complaint(Member member, Review review, ComplaintType type, String reason) {
        this.member = member;
        this.review = review;
        this.type = type;
        this.reason = reason;
        this.status = ComplaintStatus.PENDING;
    }

    public void changeStatus(ComplaintStatus status) {
        this.status = status;
    }
}
