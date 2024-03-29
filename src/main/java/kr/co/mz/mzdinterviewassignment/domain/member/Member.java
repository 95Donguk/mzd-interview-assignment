package kr.co.mz.mzdinterviewassignment.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.co.mz.mzdinterviewassignment.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("member_status != 'DELETED'")
@Table(name = "MEMBER_TBL")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberNo;

    @NotNull(message = "로그인 아이디는 필수입니다")
    @Column(unique = true)
    private String loginId;

    @NotNull(message = "이름은 필수입니다")
    private String name;

    @NotNull(message = "비밀번호는 필수입니다")
    private String password;

    @NotNull(message = "회원 상태는 필수입니다")
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

    public String delete() {
        this.memberStatus = MemberStatus.DELETED;
        return this.loginId;
    }
}
