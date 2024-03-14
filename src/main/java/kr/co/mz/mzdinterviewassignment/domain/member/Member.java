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
/**
 * @NoArgsConstructor(access = AccessLevel.PROTECTED)
 * 위 어노테이션의 의미는 매개변수 없는 생성자를 생성하는데 다른 패키지에 소속된 클래스는 접근할 수 없다라는 뜻
 * JPA는 Entity 를 인스턴스화하고 필드에 값을 채워넣기 위해 리플렉션을 사용해 런타임 시점에서 동적으로
 * 기본 생성자를 통해 클래스를 인스턴스화 하여 값을 매핑하기 때문인데
 * Entity 클래스 인스턴스를 생성할 때 매개변수 없는 기본 생성자를 사용할 일이 없기 때문에 설정
 *
 * PRIVATE로 설정하지 않은 이유는
 * JPA에서는 다른 Entity와 연관관계를 갖는 Entity를 조회할 때
 * 연관된 엔티티를 같이 가져오는 즉시 로딩과
 * 연관된 엔티티가 실제로 조회할때 가져오는 지연 로딩이 있는데
 * 즉시로딩은 엔티티 조회 시 연관된 엔티티 객체를 즉시 조회하지만
 * 지연로딩은 엔티티 조회 시 연관된 엔티티 객체는 프록시 객체로 존재하므로
 * 이때 연관된 프록시 엔티티 객체에 직접 접근 할 때 쿼리가 수행됩니다.
 *
 * JVM은 클래스 정보를 클래스 로더를 통해서 읽어와서 해당 정보를 JVM 메모리에 저장되는데
 * 리플렉션은 구체적인 클래스를 정확하게 알지 못해도 생성자, 메서드, 필드 등의 클래스 정보에 접근할 수 있도록 해주는 자바 API
 * 컴파일 시간이 아닌 실행 시간에 동적으로 특정 클래스의 정보를 추출할 수 있는 프로그래밍 기법입
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/**
 * SQL WHERE 절에 제약조건을 추가하는데 사용되는 어노테이션
 * 삭제된 회원을 WHERE 절에 추가한 이유는
 *
 * 기본 요구사항에서 회원이 물리 삭제로 구현이 된다면 회원 조회 시 삭제된 회원 정보를 보여주지 않을 것이기 때문에
 * 그 요구사항에 맞추기 위해 설정했고 삭제된 회원 데이터를 보고 싶으면 직접 데이터베이스에 접근해서 보도록 의도했습니다.
 */
@SQLRestriction("member_status != 'DELETED'")
@Table(name = "MEMBER_TBL")
public class Member extends BaseEntity {

    /**
     * 대리키로 사용한 이유는 로그인 아이디는 향후에 변경될 수 있는 속성이라고 생각하기 떄문에 기본키는 절대 바뀌지 않는 값을 사용해야한다고 생각해서 대리키로 부여했습니다.
     * - 기본키를 수정하는 것은 많은 문제를 일으킵니다. 대부분의 기본키가 여러 테이블의 FK와 인덱스로 지정되어 사용되기 때문입니다.
     * - 예로 들어 기본키을 현실 세계의 유일함을 보장하는 값인 주민등록번호, 전화 번호, 여권 번호 같은 경우는 사용자들이 직접 입력한 값일 경우가 많기 때문에 입력한 값에 오타가 있으면 그 오타를 수정하기 위해 많은 비용이 듭니다.
     * - 기본키는 절대 변경하지 않은 속성을 사용해야만 합니다. 주민등록번호, 전화 번호, 여권 번호 같은 경우는 상황에 따라 언제든지 변경될 수 있습니다.
     * - 현실 세계의 값을 기본키로 사용하는 것은 외부에 의존하는 것이라고 생각합니다. 제가 제어할 수 없는 값에 시스템이 의존해서는 안된다고 생각합니다.
     */
    @Id
    /**
     * 엔티티 기본 키를 자동으로 생성하는데 생성 전략은 데이터베이스에 위임한다는 뜻
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberNo;

    @NotNull(message = "로그인 아이디는 필수입니다")
    @Column(unique = true)
    private String loginId;

    @NotNull(message = "이름은 필수입니다")
    private String name;

    @NotNull(message = "비밀번호는 필수입니다")
    private String password;

    /**
     * 회원 삭제 여부 컬럼을 Enum으로 한 이유는
     * 처음 구현했을 당시에는 Boolean 타입으로 MariaDB에서는 bit 타입으로 false = 0 = 삭제, true = 1= 활성
     * 이렇게 값을 설정할려고 했으나 DB마다 Boolean 타입으로 저장되는 방식이 다르고 DB에서 0, 1이 아닌 다른 값이 저장되는 부분도 주의해야 한다고 생각했습니다.
     */
    @NotNull(message = "회원 상태는 필수입니다")
    /**
     * @Enumerated(EnumType.STRING)
     * - ENUM 타입을 데이터베이스에 매핑할때 ENUM 상수의 문자열 표현을 데이터베이스에 저장하고
     * - 데이터베이스에서 ENUM 상수의 문자열 표현을 ENUM 상수로 변환해준다.
     * - 즉, 상수의 문자열 표현을 저장하고 데이터베이스에서 상수의 문자열 표현을 변환해준다.
     */
    @Enumerated(EnumType.STRING)
    @Comment("ACTIVE: 계정 활성, DELETED: 계정 탈퇴")
    private MemberStatus memberStatus;

    /**
     * 빌더 패턴은 객체의 생성 과정을 유연하게 만들어주는 패턴으로
     * 빌더 패턴을 사용한 이유는
     * 1. 필요한 데이터만 설정할 수 있고
     * 2. 유연성을 확보할 수 있고
     * 3. 가독성을 높일 수 있고 ( new 연산자를 사용했을 때는 파라미터에 순서에 맞게 넣어야 하고 타입 오류가 나가 쉬운데, 빌더 패턴을 사용하면 순서가 상관없이 넣을 수 있다.)
     * 4. 불변성을 확보할 수 있다.
     */
    @Builder
    public Member(final String loginId, final String name, final String password) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.memberStatus = MemberStatus.ACTIVE;
    }

    /**
     * 논리 삭제를 한 이유
     * 보통 쇼핑몰같은 서비스에서 회원 탈퇴 같은 경우 회원 복구 기간이 있고 복구를 하면 이전에 사용했던 정보가 남아있는 것을 생각해서
     * 삭제된 회원의 데이터를 쉽게 복구 할 수 있도록 구현했고
     * 현재 서비스에서 규모가 커진다면 논리 삭제된 회원의 데이터를 분석해서 서비스 개선이나 마케팅에 활용할 수 있다고 생각합니다.
     * 논리 삭제의 장점으로는 삭제되기 전의 상태로 쉽게 되돌릴 수 있지만 데이터베이스의 저장공간을 잡아 먹는다는 점입니다.
     * 물리 삭제는 데이터를 삭제하면서 관련된 데이터와의 연결도 끊어버리므로 데이터의 일관성이 깨질 수 있습니다.
     */
    public String delete() {
        this.memberStatus = MemberStatus.DELETED;
        return this.loginId;
    }
}
