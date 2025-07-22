package shop.babsim.babsim.auth.domain;

import jakarta.persistence.*;
import lombok.*;

import shop.babsim.babsim.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApplePreSignup extends BaseEntity{

    @Column(nullable = false, unique = true)
    private String sub;

    @Column(nullable = false)
    private String email;

    private String name;

    public static ApplePreSignup of(String sub, String email, String name) {
        return ApplePreSignup.builder()
                .sub(sub)
                .email(email)
                .name(name)
                .build();
    }
}
