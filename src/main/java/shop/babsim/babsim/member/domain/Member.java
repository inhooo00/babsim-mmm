package shop.babsim.babsim.member.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.babsim.babsim.global.entity.BaseEntity;
import shop.babsim.babsim.global.entity.Status;
import shop.babsim.babsim.review.domain.Review;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String email;

    private String name;

    private String picture;

    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;

    private String nickname;

    private String introduction;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Builder
    private Member(Status status, Role role,
                   String email, String name,
                   String picture,
                   SocialType socialType,
                   String nickname,
                   String introduction) {
        this.status = status;
        this.role = role;
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.socialType = socialType;
        this.nickname = nickname;
        this.introduction = introduction;
    }

    public void updatePicture(String picture) {
        this.picture = picture;
    }
}
