package kr.co.mz.mzdinterviewassignment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 자식 엔티티 클래스들이 부모 클래스의 매핑 정보를 상속 받도록 지정하는 데 사용됩니다.
 * 지정된 클래스는는 테이블로 매핑되지 않고 매핑 정보를 상속받는 자식 엔티티들이 테이블로 매핑됩니다.
 * 엔티티 클래스는 엔티티끼리만 상속 할 수 있습니다.
 */
@MappedSuperclass
/**
 * 엔티티의 생명주기 이벤트를 감지하고 이벤트가 발생할때 실행할 리스너 클래스를 지정합니다.
 * AuditingEntityListener: Spring Data JPA 의 Auditing 기능을 제공하는데 엔티티가 생성되고 변경되는 시점에 감지됩니다.
 */
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
/**
 * 공통 매핑 정보를 담는 용도로 데이터 생성일과 수정일을 담는 클래스입니다.
 * - 데이터의 히스토리를 추적하기 위해서 입니다
 * - 데이터의 변화 과정을 파악하거나 문제가 발생했을떄 원인을 찾는데 도움이 될 수 있다고 생각했습니다.
 * - 데이터의 최신성과 정확성을 판단할 수 있으므로 신뢰도를 높일 수 있고
 * - 데이터를 정리하거나 백업할 때 관리를 쉽게 할수 있습니다.
 */
public abstract class BaseEntity {

    /**
     * Entity가 생성되어 저장될 때의 시간을 자동으로 저장한다
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * 조회한 Entity의 값을 변경할 때 시간을 자동으로 저장한다.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
