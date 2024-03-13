package kr.co.mz.mzdinterviewassignment.controller.profile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import kr.co.mz.mzdinterviewassignment.domain.profile.ProfileStatus;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.CreateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.UpdateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.profile.ProfileResponse;
import kr.co.mz.mzdinterviewassignment.facade.MemberProfileFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfileApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class ProfileApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MemberProfileFacade memberProfileFacade;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("프로필 생성 요청 테스트")
    void createProfile_Test() throws Exception {

        long memberNo = 1L;
        long profileNo = 1L;
        String nickname = "사과";
        String phoneNumber = "01011001234";
        String address = "부산광역시 해운대구 재송동 1012-1";
        ProfileStatus status = ProfileStatus.MAIN;

        CreateProfileRequest request = new CreateProfileRequest(
            nickname, phoneNumber, address);


        ProfileResponse response = ProfileResponse.builder()
            .profileNo(profileNo)
            .nickname(nickname)
            .phoneNumber(phoneNumber)
            .address(address)
            .profileStatus(status)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        given(memberProfileFacade.createProfile(any(CreateProfileRequest.class), anyLong()))
            .willReturn(response);

        mockMvc.perform(
                post("/api/members/" + memberNo + "/profiles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("회원 프로필 생성 성공"))
            .andExpect(jsonPath("$.data.profileNo").value(profileNo))
            .andExpect(jsonPath("$.data.nickname").value(nickname))
            .andExpect(jsonPath("$.data.phoneNumber").value(phoneNumber))
            .andExpect(jsonPath("$.data.address").value(address))
            .andExpect(jsonPath("$.data.profileStatus").value(status.name()))
            .andDo(print());

        verify(memberProfileFacade, times(1)).createProfile(any(CreateProfileRequest.class),
            anyLong());
    }

    @Test
    @DisplayName("프로필 수정 요청 테스트")
    void updateProfile_Test() throws Exception {

        long memberNo = 1L;
        long profileNo = 1L;

        String nickname = "사과";
        String phoneNumber = "01011001234";
        String address = "부산광역시 해운대구 재송동 1012-1";
        ProfileStatus status = ProfileStatus.NORMAL;

        UpdateProfileRequest dto = new UpdateProfileRequest(
            nickname,
            phoneNumber,
            address,
            status
        );

        ProfileResponse response = ProfileResponse.builder()
            .profileNo(profileNo)
            .nickname(nickname)
            .phoneNumber(phoneNumber)
            .address(address)
            .profileStatus(status)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        given(memberProfileFacade.updateProfile(any(UpdateProfileRequest.class),
            anyLong(),
            anyLong())).willReturn(response);

        mockMvc.perform(
                patch("/api/members/" + memberNo + "/profiles/" + profileNo)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("회원 프로필 수정 성공"))
            .andExpect(jsonPath("$.data.nickname").value(dto.getNickname()))
            .andExpect(jsonPath("$.data.phoneNumber").value(dto.getPhoneNumber()))
            .andExpect(jsonPath("$.data.address").value(dto.getAddress()))
            .andExpect(jsonPath("$.data.profileStatus").value(dto.getProfileStatus().name()))
            .andExpect(jsonPath("$.data.createdAt").exists())
            .andExpect(jsonPath("$.data.updatedAt").exists())
            .andDo(print());

        verify(memberProfileFacade, times(1))
            .updateProfile(any(UpdateProfileRequest.class), anyLong(), anyLong());
    }

    @Test
    @DisplayName("프로필 삭제 요청 테스트")
    void deleteProfile() throws Exception {

        long memberNo = 1L;
        long profileNo = 1L;

        String nickname = "사과";

        given(memberProfileFacade.deleteProfile(anyLong(), anyLong()))
            .willReturn(nickname);

        mockMvc.perform(
                delete("/api/members/" + memberNo + "/profiles/" + profileNo))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("회원 프로필 삭제 성공"))
            .andExpect(jsonPath("$.data").value("삭제된 프로필 닉네임 : " + nickname))
            .andDo(print());

        verify(memberProfileFacade, times(1)).deleteProfile(anyLong(), anyLong());
    }
}