package com.example.winter_olympics;

import com.example.winter_olympics.config.SecurityConfig;
import com.example.winter_olympics.controller.CompetitionController;
import com.example.winter_olympics.dto.TestDataResponse;
import com.example.winter_olympics.entity.Competition;
import com.example.winter_olympics.repository.CompetitionRepository;
import com.example.winter_olympics.repository.UserRepository;
import com.example.winter_olympics.service.JwtService;
import com.example.winter_olympics.service.TestDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        com.example.winter_olympics.controller.AdminTestDataController.class,
        CompetitionController.class
})
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class AdminTestDataSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TestDataService testDataService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CompetitionRepository competitionRepository;

    @Test
    void adminCanCreateTestData() throws Exception {
        when(testDataService.createDemoData()).thenReturn(new TestDataResponse(
                "Reused", 0, 4, 0, 8, 0, 4, 0, 16, 0, 8, 0, 8
        ));

        mockMvc.perform(post("/api/admin/test-data").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void athleteCannotCreateTestData() throws Exception {
        mockMvc.perform(post("/api/admin/test-data").with(user("athlete").roles("ATHLETE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserCannotCreateTestData() throws Exception {
        mockMvc.perform(post("/api/admin/test-data"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void competitionReadsRemainPublicAndWritesRemainAdminOnly() throws Exception {
        when(competitionRepository.findAll()).thenReturn(java.util.List.of());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/competitions"))
                .andExpect(status().isOk());

        String body = """
                {"name":"Demo","type":"SKI_SLALOM","gender":"MALE","minimumAge":18,"numberOfLaps":null,"shootingAfterLaps":null}
                """;
        mockMvc.perform(post("/api/competitions").with(user("athlete").roles("ATHLETE"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/competitions/1")
                        .with(user("athlete").roles("ATHLETE")))
                .andExpect(status().isForbidden());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/competitions/1")
                        .with(user("athlete").roles("ATHLETE")))
                .andExpect(status().isForbidden());

        when(competitionRepository.save(any(Competition.class))).thenAnswer(invocation -> invocation.getArgument(0));
        mockMvc.perform(post("/api/competitions").with(user("admin").roles("ADMIN"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }
}
