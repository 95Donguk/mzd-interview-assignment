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

/**
 * 비식별 관계로 설정한 이유는?
 * - 데이터베이스 설계 관점에서보면 식별 관계는 부모 테이블의 기본 키를 자식 테이블로 전파하면서 자식 테이블의 기본키 칼럼이 점점 늘어나기에 SQL 성능을 저하시킵니다.
 * - 식별 관계는 일반적으로 복합 기본 키를 만들어야 하는 경우가 많습니다.
 * - 식별 관계를 사용할 때 기본 키로 자연 키 칼럼을 조합하는 경우가 많은데 비즈니스 요구사항은 시간이 지나고 변할 수 있기 때문에 비즈니스와 관련 없는 인조 키를 사용하는 것이 좋습니다.
 * - 식별 관계에서 자식 테이블은 부모 테이블의 기본 키를 자신의 기본 키로 사용함으로 테이블 구조가 유연하지 못합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PROFILE_TBL")
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileNo;

    @NotNull(message = "별명은 필수입니다")
    private String nickname;

    @NotNull(message = "휴대전화 번호는 필수입니다")
    private String phoneNumber;

    private String address;

    @NotNull(message = "프로필 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Comment("MAIN: 메인 프로필, NORMAL: 일반 프로필")
    private ProfileStatus profileStatus;

    /**
     * 지연로딩을 한 이유
     * 즉시로딩은 예측이 어렵고 어떤 SQL이 실행될지 추적하기 어렵습니다.
     * 특히 JPQL을 실행할 때 N+1 문제가 자주 발생합니다.
     * - JPQL는 객체지향 쿼리 언어로, 엔티티 객체에 대한 데이터베이스 조회 및 조작을 표준화한 방법으로
     * - 데이터베이스 의존적이지 않고 타입 안정성을 체크할 수 있습니다.
     *
     * 단방향 연관관계로 설정한 이유는
     * 양방향 연관관계보다 관리가 쉽고 성능상 이점이 있어서 설정했습니다.
     * 양방향 연관관계는 연관관계 주인도 정해야하고 양방향을 지키기 위해 로직과 객체를 잘 관리 해야하기 때문에 복잡하고
     * 단방향과 비교해서 양방향의 장점은 반대 방향으로 객체 그래프 탐색 기능(객체 연관관계를 사용한 조회)이 추가된 것 뿐이기 때문입니다.
     */
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
