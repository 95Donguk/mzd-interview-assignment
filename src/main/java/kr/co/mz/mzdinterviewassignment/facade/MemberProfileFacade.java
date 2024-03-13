package kr.co.mz.mzdinterviewassignment.facade;

import java.util.List;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.dto.request.member.CreateMemberRequest;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.CreateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.UpdateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberDetailsResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberInfoResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.profile.ProfileResponse;
import kr.co.mz.mzdinterviewassignment.service.MemberService;
import kr.co.mz.mzdinterviewassignment.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileFacade {

    private final MemberService memberService;
    private final ProfileService profileService;

    @Transactional
    public MemberResponse createMember(final CreateMemberRequest dto) {
        Member member = memberService.createMember(dto);
        ProfileResponse response = profileService.createProfile(dto.getProfile(), member);
        return MemberResponse.generateMemberResponse(member, response);
    }

    @Transactional
    public String deleteMember(final Long memberNo) {
        return memberService.deleteMember(memberNo);
    }

    public MemberDetailsResponse findMemberDetails(final Long memberNo) {
        Member member = memberService.findMember(memberNo);
        List<ProfileResponse> responses = profileService.findProfiles(member);

        return MemberDetailsResponse.generateMemberDetails(member, responses);
    }

    public List<MemberInfoResponse> findMembers(final int page, final int size, final String name) {
        Page<Member> members = memberService.findMembersContainName(name, page, size);

        return members.stream().map(member -> {
            ProfileResponse response = profileService.findMainProfile(member);
            return MemberInfoResponse.generateMemberInfo(member, response);
        }).toList();
    }

    @Transactional
    public ProfileResponse createProfile(final CreateProfileRequest dto, final Long memberNo) {
        Member member = memberService.findMember(memberNo);
        return profileService.createProfile(dto, member);
    }

    @Transactional
    public ProfileResponse updateProfile(final UpdateProfileRequest dto,
                                         final Long profileNo,
                                         final Long memberNo) {
        Member member = memberService.findMember(memberNo);
        return profileService.updateProfile(dto, profileNo, member);
    }

    @Transactional
    public String deleteProfile(final Long profileNo, final Long memberNo) {
        Member member = memberService.findMember(memberNo);
        return profileService.deleteProfile(profileNo, member);
    }
}
