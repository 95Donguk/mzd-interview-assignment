package kr.co.mz.mzdinterviewassignment.repository;

import java.util.Optional;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA를 사용한 이유
 * 데이터베이스에 종속적이지 않고
 * 객체와 관계형 데이터베이스 의 데이터를 자동으로 매핑해주고
 * 또한 복잡한 쿼리와 SQL 제어가 필요없고 객체 모델링도 손쉽게 할 수 있도록 도와준다.
 * ORM을 통해 개발자는 비즈니스 로직에 좀 더 집중할 수 있습니다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * SELECT * FROM member WHERE login_id = :loginId;
     */
    Optional<Member> findByLoginId(final String loginId);

    /**
     * SELECT * FROM member WHERE name LIKE '%name%' LIMIT {offset}, {pageSize}
     */
    Page<Member> findMembersByNameContaining(final String name, final Pageable pageable);
}
