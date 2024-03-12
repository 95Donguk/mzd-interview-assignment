package kr.co.mz.mzdinterviewassignment.controller.member;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kr.co.mz.mzdinterviewassignment.domain.member.MemberStatus;
import kr.co.mz.mzdinterviewassignment.domain.profile.ProfileStatus;
import kr.co.mz.mzdinterviewassignment.dto.request.member.CreateMemberRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberDetailsResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberInfoResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.profile.ProfileResponse;
import kr.co.mz.mzdinterviewassignment.facade.MemberProfileFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MemberApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MemberProfileFacade memberProfileFacade;

    @Test
    @DisplayName("회원 생성 요청 테스트")
    void createMember_Test() throws Exception {

        CreateMemberRequest request = new CreateMemberRequest(
            "test1",
            "테스트",
            "test123@"
        );

        given(memberProfileFacade.createMember(request))
            .willReturn(MemberResponse.builder()
                .memberNo(1L)
                .loginId("test1")
                .name("테스트")
                .createdAt(LocalDateTime.now())
                .build());

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(
                post("/api/members")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(header().exists("location"))
            .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.name()))
            .andExpect(jsonPath("$.message").value("회원 생성 성공"))
            .andExpect(jsonPath("$.data.memberNo").value(1L))
            .andExpect(jsonPath("$.data.loginId").value("test1"))
            .andExpect(jsonPath("$.data.name").value("테스트"))
            .andExpect(jsonPath("$.data.createAt").exists())
            .andDo(print());

        verify(memberProfileFacade).createMember(request);
    }

    @Test
    @DisplayName("회원 삭제 요청 테스트")
    void deleteMember_Test() throws Exception {

        given(memberProfileFacade.deleteMember(1L))
            .willReturn("test1");

        String memberNo = "1";

        mockMvc.perform(
                delete("/api/members/" + memberNo))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("회원 삭제 성공"))
            .andExpect(jsonPath("$.data").value("삭제된 회원 아이디 test1"))
            .andDo(print());

        verify(memberProfileFacade, times(1)).deleteMember(1L);
        ;
    }

    @Test
    @DisplayName("회원 상세 정보 조회 요청 테스트")
    void findMember_Test() throws Exception {

        given(memberProfileFacade.findMemberDetails(1L))
            .willReturn(MemberDetailsResponse.builder()
                .memberNo(1L)
                .loginId("hong123")
                .name("홍길동")
                .password("honggil12!")
                .memberStatus(MemberStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .profiles(Collections.singletonList(
                    ProfileResponse.builder()
                        .profileNo(1L)
                        .nickname("홍시")
                        .phoneNumber("01012345678")
                        .address("서울특별시 종로구 청계천로 85 17층(관철동, 삼일빌딩) 한국지역정보개발원")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .profileStatus(ProfileStatus.MAIN)
                        .build()))
                .build());

        String memberNo = "1";

        mockMvc.perform(
                get("/api/members/" + memberNo))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.name()))
            .andExpect(jsonPath("$.message").value("회원 상세 조회 성공"))
            .andExpect(jsonPath("$.data.memberNo").value(1L))
            .andExpect(jsonPath("$.data.loginId").value("hong123"))
            .andExpect(jsonPath("$.data.name").value("홍길동"))
            .andExpect(jsonPath("$.data.password").value("honggil12!"))
            .andExpect(jsonPath("$.data.memberStatus").value(MemberStatus.ACTIVE.name()))
            .andExpect(jsonPath("$.data.profiles[0].profileNo").value(1L))
            .andExpect(jsonPath("$.data.profiles[0].nickname").value("홍시"))
            .andExpect(jsonPath("$.data.profiles[0].phoneNumber").value("01012345678"))
            .andDo(print());

        verify(memberProfileFacade).findMemberDetails(1L);
    }

    @Test
    @DisplayName("회원 전체 조회 요청 테스트")
    void findMembers_Test() throws Exception {

        List<MemberInfoResponse> data = new ArrayList<>();
        data.add(MemberInfoResponse.builder()
            .memberNo(1L)
            .loginId("test1")
            .name("홍길동")
            .password("test123@")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .memberStatus(MemberStatus.ACTIVE)
            .mainProfile(ProfileResponse.builder()
                .profileNo(1L)
                .nickname("홍시")
                .phoneNumber("01012345678")
                .address("서울특별시 종로구 청계천로 85 17층(관철동, 삼일빌딩) 한국지역정보개발원")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .profileStatus(ProfileStatus.MAIN)
                .build())
            .build());
        data.add(MemberInfoResponse.builder()
            .memberNo(2L)
            .loginId("honggil123")
            .name("엄홍길")
            .password("mountain2033!")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .memberStatus(MemberStatus.ACTIVE)
            .mainProfile(ProfileResponse.builder()
                .profileNo(2L)
                .nickname("산악대장")
                .phoneNumber("0112345678")
                .address("서울특별시 성북구 화랑도 11길 26 103동 1602호 (하월곡동, OO아파트)")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .profileStatus(ProfileStatus.MAIN)
                .build())
            .build());

        given(memberProfileFacade.findMembers(0, 5, "홍길"))
            .willReturn(data);

        mockMvc.perform(
                get("/api/members")
                    .param("page", "0")
                    .param("size", "5")
                    .param("name", "홍길"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("회원 전체 조회 성공"))
            .andExpect(jsonPath("$.data").isArray());

        verify(memberProfileFacade, times(1)).findMembers(0, 5, "홍길");
    }
}