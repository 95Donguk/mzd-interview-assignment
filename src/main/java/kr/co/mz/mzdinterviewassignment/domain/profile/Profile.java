package kr.co.mz.mzdinterviewassignment.domain.profile;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.co.mz.mzdinterviewassignment.domain.BaseEntity;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PROFILE_TBL")
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileNo;

    @NotNull
    private String nickname;

    @NotNull
    private String phoneNumber;

    private String address;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Comment("MAIN: 메인 프로필, NORMAL: 일반 프로필")
    private ProfileStatus profileStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;

    @Builder
    public Profile(final String nickname, final String phoneNumber, final String address,
                   final ProfileStatus profileStatus, final Member member) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.profileStatus = profileStatus;
        this.member = member;
    }

    public Profile update(final String nickname, final String phoneNumber, final String address,
                          final ProfileStatus profileStatus) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.profileStatus = profileStatus;
        return this;
    }

    public void updateProfileStatus(final ProfileStatus profileStatus) {
        this.profileStatus = profileStatus;
    }
}
