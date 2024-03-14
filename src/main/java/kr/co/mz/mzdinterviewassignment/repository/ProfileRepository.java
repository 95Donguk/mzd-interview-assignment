package kr.co.mz.mzdinterviewassignment.repository;

import java.util.List;
import java.util.Optional;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.domain.profile.Profile;
import kr.co.mz.mzdinterviewassignment.domain.profile.ProfileStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * SELECT * FROM profile WHERE member_id = :memberId AND profile_status = :profileStatus;
     */
    Optional<Profile> findProfileByMemberAndProfileStatus(final Member member,
                                                          final ProfileStatus profileStatus);

    /**
     * SELECT * FROM profile WHERE member_id = :memberId;
     */
    List<Profile> findAllByMember(final Member member);
}
