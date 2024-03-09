package kr.co.mz.mzdinterviewassignment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberNo;

    @NotNull
    @Column(unique = true)
    private String loginId;

    @NotNull
    private String name;

    @NotNull
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Comment("ACTIVE: 계정 활성, DELETED: 계정 탈퇴")
    private MemberStatus memberStatus;

    @Builder
    public Member(final String loginId, final String name, final String password) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.memberStatus = MemberStatus.ACTIVE;
    }

    public Member update(final String loginId, final String name, final String password) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        return this;
    }

    public void delete() {
        this.memberStatus = MemberStatus.DELETED;
    }
}
