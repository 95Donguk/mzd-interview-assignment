package kr.co.mz.mzdinterviewassignment.repository;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(final String loginId);

    Page<Member> findMembersByNameContaining(final String name, final Pageable pageable);
}
