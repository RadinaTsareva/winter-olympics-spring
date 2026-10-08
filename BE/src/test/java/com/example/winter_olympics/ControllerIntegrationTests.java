package com.example.winter_olympics;

import com.example.winter_olympics.controller.AthleteController;
import com.example.winter_olympics.controller.AuthController;
import com.example.winter_olympics.controller.BiathlonResultController;
import com.example.winter_olympics.controller.CompetitionController;
import com.example.winter_olympics.controller.CompetitionRegistrationController;
import com.example.winter_olympics.controller.CountryController;
import com.example.winter_olympics.controller.OlympicController;
import com.example.winter_olympics.controller.SlalomResultController;
import com.example.winter_olympics.dto.AuthRequest;
import com.example.winter_olympics.dto.AthleteRequest;
import com.example.winter_olympics.dto.BiathlonRankingResponse;
import com.example.winter_olympics.dto.BiathlonResultRequest;
import com.example.winter_olympics.dto.BiathlonResultResponse;
import com.example.winter_olympics.dto.CompetitionRequest;
import com.example.winter_olympics.dto.CountryMedalResponse;
import com.example.winter_olympics.dto.OlympicStatisticsResponse;
import com.example.winter_olympics.dto.RegistrationRequest;
import com.example.winter_olympics.dto.SlalomRankingResponse;
import com.example.winter_olympics.dto.SlalomResultRequest;
import com.example.winter_olympics.dto.SlalomResultResponse;
import com.example.winter_olympics.entity.Athlete;
import com.example.winter_olympics.entity.BiathlonResult;
import com.example.winter_olympics.entity.Competition;
import com.example.winter_olympics.entity.CompetitionRegistration;
import com.example.winter_olympics.entity.CompetitionType;
import com.example.winter_olympics.entity.Country;
import com.example.winter_olympics.entity.Gender;
import com.example.winter_olympics.entity.Medal;
import com.example.winter_olympics.entity.Role;
import com.example.winter_olympics.entity.SlalomResult;
import com.example.winter_olympics.entity.User;
import com.example.winter_olympics.repository.AthleteRepository;
import com.example.winter_olympics.repository.BiathlonResultRepository;
import com.example.winter_olympics.repository.CompetitionRepository;
import com.example.winter_olympics.repository.CompetitionRegistrationRepository;
import com.example.winter_olympics.repository.CountryRepository;
import com.example.winter_olympics.repository.UserRepository;
import com.example.winter_olympics.service.BiathlonService;
import com.example.winter_olympics.service.JwtService;
import com.example.winter_olympics.service.OlympicStatisticsService;
import com.example.winter_olympics.service.SlalomService;
import com.example.winter_olympics.repository.SlalomResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        AuthController.class,
        AthleteController.class,
        CompetitionController.class,
        CompetitionRegistrationController.class,
        CountryController.class,
        BiathlonResultController.class,
        SlalomResultController.class,
        OlympicController.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import(com.example.winter_olympics.config.GlobalExceptionHandler.class)
class ControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AthleteRepository athleteRepository;

    @MockitoBean
    private CountryRepository countryRepository;

    @MockitoBean
    private CompetitionRepository competitionRepository;

    @MockitoBean
    private CompetitionRegistrationRepository registrationRepository;

    @MockitoBean
    private BiathlonResultRepository biathlonResultRepository;

    @MockitoBean
    private SlalomResultRepository slalomResultRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private BiathlonService biathlonService;

    @MockitoBean
    private SlalomService slalomService;

    @MockitoBean
    private OlympicStatisticsService statisticsService;

    @Test
    void registerReturnsTokenAndUserInfo() throws Exception {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", 1L);
            return user;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthRequest request = new AuthRequest("newuser", "secret", null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("ATHLETE"));
    }

    @Test
    void registerFailsWhenUsernameAlreadyExists() throws Exception {
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(new AuthRequest("existing", "secret", null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void loginReturnsTokenAndRole() throws Exception {
        User user = sampleUser(1L, "admin", "encoded-secret", Role.ADMIN, null);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded-secret")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("login-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(new AuthRequest("admin", "secret", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("login-token"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void getAllCountriesReturnsData() throws Exception {
        when(countryRepository.findAll()).thenReturn(List.of(country(1L, "Norway")));

        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Norway"));
    }

    @Test
    void getCountryByIdReturnsCountry() throws Exception {
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country(1L, "Sweden")));

        mockMvc.perform(get("/api/countries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Sweden"));
    }

    @Test
    void createCountryStoresCountry() throws Exception {
        when(countryRepository.save(any(Country.class))).thenAnswer(invocation -> {
            Country country = invocation.getArgument(0);
            ReflectionTestUtils.setField(country, "id", 5L);
            return country;
        });

        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(new Country("Finland"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("Finland"));
    }

    @Test
    void updateCountryChangesName() throws Exception {
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country(1L, "Old name")));
        when(countryRepository.save(any(Country.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(new Country("New name"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New name"));
    }

    @Test
    void deleteCountryReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/countries/1"))
                .andExpect(status().isOk());

        verify(countryRepository).deleteById(1L);
    }

    @Test
    void getAllAthletesReturnsResponses() throws Exception {
        when(athleteRepository.findAll()).thenReturn(List.of(
                athlete(1L, "Ava", country(1L, "Norway"), Gender.FEMALE, LocalDate.of(2000, 1, 1))
        ));

        mockMvc.perform(get("/api/athletes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Ava"))
                .andExpect(jsonPath("$[0].country").value("Norway"));
    }

    @Test
    void getAthleteByIdReturnsAthlete() throws Exception {
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(
                athlete(1L, "Ola", country(2L, "Sweden"), Gender.MALE, LocalDate.of(1998, 5, 20))
        ));

        mockMvc.perform(get("/api/athletes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ola"))
                .andExpect(jsonPath("$.country").value("Sweden"));
    }

    @Test
    void createAthleteSavesAthlete() throws Exception {
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country(1L, "Canada")));
        when(athleteRepository.save(any(Athlete.class))).thenAnswer(invocation -> {
            Athlete athlete = invocation.getArgument(0);
            ReflectionTestUtils.setField(athlete, "id", 9L);
            return athlete;
        });

        AthleteRequest request = new AthleteRequest();
        request.setName("Mia");
        request.setCountryId(1L);
        request.setGender(Gender.FEMALE);
        request.setDateOfBirth(LocalDate.of(2002, 4, 12));

        mockMvc.perform(post("/api/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(9L))
                .andExpect(jsonPath("$.name").value("Mia"));
    }

    @Test
    void updateAthleteAllowsOwner() throws Exception {
        Country country = country(2L, "France");
        Athlete existing = athlete(1L, "Old", country(1L, "Italy"), Gender.MALE, LocalDate.of(2000, 1, 1));
        Athlete ownerAthlete = athlete(1L, "Old", country, Gender.MALE, LocalDate.of(2000, 1, 1));
        User user = sampleUser(1L, "athlete1", "pw", Role.ATHLETE, ownerAthlete);

        when(userRepository.findByUsername("athlete1")).thenReturn(Optional.of(user));
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(countryRepository.findById(2L)).thenReturn(Optional.of(country));
        when(athleteRepository.save(any(Athlete.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AthleteRequest request = new AthleteRequest();
        request.setName("Updated name");
        request.setCountryId(2L);
        request.setGender(Gender.MALE);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        mockMvc.perform(put("/api/athletes/1")
                        .with(authentication("athlete1", "ROLE_ATHLETE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated name"))
                .andExpect(jsonPath("$.country.name").value("France"));
    }

    @Test
    void updateAthleteRejectsNonOwner() throws Exception {
        User user = sampleUser(2L, "other", "pw", Role.ATHLETE, athlete(2L, "Other", country(3L, "USA"), Gender.MALE, LocalDate.of(1990, 1, 1)));
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(user));

        AthleteRequest request = new AthleteRequest();
        request.setName("Updated name");
        request.setCountryId(2L);
        request.setGender(Gender.MALE);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        mockMvc.perform(put("/api/athletes/1")
                        .with(authentication("other", "ROLE_ATHLETE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You can only modify your own athlete data"));
    }

    @Test
    void deleteAthleteAsAdminSucceeds() throws Exception {
        mockMvc.perform(delete("/api/athletes/1")
                        .with(authentication("admin", "ROLE_ADMIN")))
                .andExpect(status().isNoContent());

        verify(athleteRepository).deleteById(1L);
    }

    @Test
    void getAllCompetitionsReturnsCompetitions() throws Exception {
        when(competitionRepository.findAll()).thenReturn(List.of(
                competition(1L, "Sprint", CompetitionType.SKI_SLALOM, Gender.MALE, 18, null, null)
        ));

        mockMvc.perform(get("/api/competitions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sprint"))
                .andExpect(jsonPath("$[0].type").value("SKI_SLALOM"));
    }

    @Test
    void getCompetitionByIdReturnsCompetition() throws Exception {
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(
                competition(1L, "Biathlon Final", CompetitionType.BIATHLON, Gender.FEMALE, 21, 5, 2)
        ));

        mockMvc.perform(get("/api/competitions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Biathlon Final"))
                .andExpect(jsonPath("$.type").value("BIATHLON"));
    }

    @Test
    void createBiathlonCompetitionSavesCompetition() throws Exception {
        when(competitionRepository.save(any(Competition.class))).thenAnswer(invocation -> {
            Competition competition = invocation.getArgument(0);
            ReflectionTestUtils.setField(competition, "id", 11L);
            return competition;
        });

        CompetitionRequest request = new CompetitionRequest();
        request.setName("World Cup");
        request.setType(CompetitionType.BIATHLON);
        request.setGender(Gender.FEMALE);
        request.setMinimumAge(20);
        request.setNumberOfLaps(5);
        request.setShootingAfterLaps(2);

        mockMvc.perform(post("/api/competitions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11L))
                .andExpect(jsonPath("$.name").value("World Cup"))
                .andExpect(jsonPath("$.type").value("BIATHLON"));
    }

    @Test
    void createSlalomCompetitionRejectsLapSettings() throws Exception {
        CompetitionRequest request = new CompetitionRequest();
        request.setName("Slalom");
        request.setType(CompetitionType.SKI_SLALOM);
        request.setGender(Gender.MALE);
        request.setMinimumAge(18);
        request.setNumberOfLaps(1);
        request.setShootingAfterLaps(1);

        mockMvc.perform(post("/api/competitions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Lap and shooting settings are only allowed for biathlon"));
    }

    @Test
    void updateCompetitionChangesFields() throws Exception {
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(
                competition(1L, "Old", CompetitionType.SKI_SLALOM, Gender.MALE, 18, null, null)
        ));
        when(competitionRepository.save(any(Competition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompetitionRequest request = new CompetitionRequest();
        request.setName("New");
        request.setType(CompetitionType.SKI_SLALOM);
        request.setGender(Gender.FEMALE);
        request.setMinimumAge(19);

        mockMvc.perform(put("/api/competitions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.gender").value("FEMALE"));
    }

    @Test
    void deleteCompetitionReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/competitions/1"))
                .andExpect(status().isNoContent());

        verify(competitionRepository).deleteById(1L);
    }

    @Test
    void getAllRegistrationsAsAdminReturnsAll() throws Exception {
        CompetitionRegistration registration = registration(1L,
                athlete(1L, "Anna", country(1L, "Norway"), Gender.FEMALE, LocalDate.of(2000, 1, 1)),
                competition(1L, "Sprint", CompetitionType.SKI_SLALOM, Gender.FEMALE, 18, null, null));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(sampleUser(1L, "admin", "pw", Role.ADMIN, null)));
        when(registrationRepository.findAll()).thenReturn(List.of(registration));

        mockMvc.perform(get("/api/registrations").with(authentication("admin", "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].athleteName").value("Anna"));
    }

    @Test
    void getAllRegistrationsAsAthleteFiltersToOwn() throws Exception {
        Athlete athlete = athlete(1L, "Anna", country(1L, "Norway"), Gender.FEMALE, LocalDate.of(2000, 1, 1));
        User user = sampleUser(1L, "athlete", "pw", Role.ATHLETE, athlete);
        CompetitionRegistration own = registration(1L, athlete,
                competition(1L, "Sprint", CompetitionType.SKI_SLALOM, Gender.FEMALE, 18, null, null));
        CompetitionRegistration other = registration(2L,
                athlete(2L, "Bob", country(2L, "Sweden"), Gender.MALE, LocalDate.of(1999, 1, 1)),
                competition(2L, "Biathlon", CompetitionType.BIATHLON, Gender.MALE, 18, 5, 2));

        when(userRepository.findByUsername("athlete")).thenReturn(Optional.of(user));
        when(registrationRepository.findAll()).thenReturn(List.of(own, other));

        mockMvc.perform(get("/api/registrations").with(authentication("athlete", "ROLE_ATHLETE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$.length()")
                        .value(1));
    }

    @Test
    void createRegistrationSavesWhenValid() throws Exception {
        Athlete athlete = athlete(1L, "Anna", country(1L, "Norway"), Gender.FEMALE, LocalDate.now().minusYears(25));
        Competition competition = competition(1L, "Sprint", CompetitionType.SKI_SLALOM, Gender.FEMALE, 18, null, null);
        User user = sampleUser(1L, "athlete", "pw", Role.ATHLETE, athlete);
        CompetitionRegistration saved = registration(7L, athlete, competition);

        when(userRepository.findByUsername("athlete")).thenReturn(Optional.of(user));
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(registrationRepository.existsByAthleteIdAndCompetitionId(1L, 1L)).thenReturn(false);
        when(registrationRepository.save(any(CompetitionRegistration.class))).thenReturn(saved);

        RegistrationRequest request = new RegistrationRequest();
        request.setAthleteId(1L);
        request.setCompetitionId(1L);

        mockMvc.perform(post("/api/registrations")
                        .with(authentication("athlete", "ROLE_ATHLETE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.athleteName").value("Anna"));
    }

    @Test
    void createRegistrationRejectsWrongAthlete() throws Exception {
        Athlete athlete = athlete(2L, "Bob", country(2L, "Sweden"), Gender.MALE, LocalDate.now().minusYears(25));
        Competition competition = competition(1L, "Sprint", CompetitionType.SKI_SLALOM, Gender.MALE, 18, null, null);
        User user = sampleUser(1L, "athlete", "pw", Role.ATHLETE, athlete(1L, "Anna", country(1L, "Norway"), Gender.FEMALE, LocalDate.now().minusYears(25)));

        when(userRepository.findByUsername("athlete")).thenReturn(Optional.of(user));
        when(athleteRepository.findById(2L)).thenReturn(Optional.of(athlete));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        RegistrationRequest request = new RegistrationRequest();
        request.setAthleteId(2L);
        request.setCompetitionId(1L);

        mockMvc.perform(post("/api/registrations")
                        .with(authentication("athlete", "ROLE_ATHLETE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You can only register yourself for competitions"));
    }

    @Test
    void deleteRegistrationAsAdminSucceeds() throws Exception {
        CompetitionRegistration registration = registration(1L,
                athlete(1L, "Anna", country(1L, "Norway"), Gender.FEMALE, LocalDate.of(2000, 1, 1)),
                competition(1L, "Sprint", CompetitionType.SKI_SLALOM, Gender.FEMALE, 18, null, null));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(sampleUser(1L, "admin", "pw", Role.ADMIN, null)));
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));

        mockMvc.perform(delete("/api/registrations/1").with(authentication("admin", "ROLE_ADMIN")))
                .andExpect(status().isNoContent());

        verify(registrationRepository).delete(registration);
    }

    @Test
    void saveBiathlonResultReturnsResponse() throws Exception {
        Competition competition = competition(1L, "Biathlon", CompetitionType.BIATHLON, Gender.MALE, 18, 5, 2);
        Athlete athlete = athlete(1L, "Erik", country(1L, "Norway"), Gender.MALE, LocalDate.of(1999, 1, 1));
        CompetitionRegistration registration = registration(1L, athlete, competition);
        BiathlonResult result = new BiathlonResult(registration, new BigDecimal("0.5"));
        result.setSkiTime(new BigDecimal("123.456"));
        result.setMisses(2);
        result.setFinished(true);

        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(biathlonResultRepository.findAll()).thenReturn(List.of());
        when(biathlonResultRepository.save(any(BiathlonResult.class))).thenReturn(result);
        when(biathlonService.toResponse(result)).thenReturn(new BiathlonResultResponse(
                1L,
                "Erik",
                "Norway",
                new BigDecimal("123.456"),
                2,
                new BigDecimal("0.5"),
                new BigDecimal("1.0"),
                new BigDecimal("124.456"),
                true
        ));

        BiathlonResultRequest request = new BiathlonResultRequest();
        request.setRegistrationId(1L);
        request.setSkiTime(new BigDecimal("123.456"));
        request.setMisses(2);
        request.setPenaltyPerMiss(new BigDecimal("0.5"));
        request.setFinished(true);

        mockMvc.perform(post("/api/biathlon-results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.athleteName").value("Erik"))
                .andExpect(jsonPath("$.finalTime").value(124.456));
    }

    @Test
    void saveBiathlonResultRejectsWrongCompetitionType() throws Exception {
        Competition competition = competition(1L, "Slalom", CompetitionType.SKI_SLALOM, Gender.MALE, 18, null, null);
        Athlete athlete = athlete(1L, "Erik", country(1L, "Norway"), Gender.MALE, LocalDate.of(1999, 1, 1));
        CompetitionRegistration registration = registration(1L, athlete, competition);
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));

        BiathlonResultRequest request = new BiathlonResultRequest();
        request.setRegistrationId(1L);
        request.setSkiTime(new BigDecimal("123.456"));
        request.setMisses(2);
        request.setPenaltyPerMiss(new BigDecimal("0.5"));
        request.setFinished(true);

        mockMvc.perform(post("/api/biathlon-results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Registration is not for a biathlon competition"));
    }

    @Test
    void getBiathlonRankingReturnsRanking() throws Exception {
        when(biathlonService.getRankingResponse(1L)).thenReturn(List.of(
                new BiathlonRankingResponse(1, "Erik", "Norway", new BigDecimal("123.456"), 1, new BigDecimal("0.5"), new BigDecimal("123.956"))
        ));

        mockMvc.perform(get("/api/biathlon-results/ranking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].position").value(1))
                .andExpect(jsonPath("$[0].athleteName").value("Erik"));
    }

    @Test
    void saveFirstRunForSlalomReturnsResult() throws Exception {
        Competition competition = competition(1L, "Slalom", CompetitionType.SKI_SLALOM, Gender.FEMALE, 18, null, null);
        Athlete athlete = athlete(1L, "Anna", country(1L, "Norway"), Gender.FEMALE, LocalDate.of(2001, 1, 1));
        CompetitionRegistration registration = registration(1L, athlete, competition);
        SlalomResult result = new SlalomResult(registration);
        result.setFirstRunTime(new BigDecimal("55.123"));
        result.setFirstRunFinished(true);
        result.setSecondRunTime(null);
        result.setSecondRunFinished(false);

        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(slalomResultRepository.findAll()).thenReturn(List.of());
        when(slalomResultRepository.save(any(SlalomResult.class))).thenReturn(result);

        SlalomResultRequest request = new SlalomResultRequest();
        request.setRegistrationId(1L);
        request.setTime(new BigDecimal("55.123"));
        request.setFinished(true);

        mockMvc.perform(post("/api/slalom-results/first-run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstRunTime").value(55.123))
                .andExpect(jsonPath("$.firstRunFinished").value(true));
    }

    @Test
    void saveSecondRunForSlalomReturnsResult() throws Exception {
        Competition competition = competition(1L, "Slalom", CompetitionType.SKI_SLALOM, Gender.FEMALE, 18, null, null);
        Athlete athlete = athlete(1L, "Anna", country(1L, "Norway"), Gender.FEMALE, LocalDate.of(2001, 1, 1));
        CompetitionRegistration registration = registration(1L, athlete, competition);
        SlalomResult existing = new SlalomResult(registration);
        existing.setFirstRunTime(new BigDecimal("55.123"));
        existing.setFirstRunFinished(true);
        SlalomResult saved = new SlalomResult(registration);
        saved.setFirstRunTime(new BigDecimal("55.123"));
        saved.setFirstRunFinished(true);
        saved.setSecondRunTime(new BigDecimal("54.222"));
        saved.setSecondRunFinished(true);

        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(slalomResultRepository.findAll()).thenReturn(List.of(existing));
        when(slalomService.getSecondRunParticipants(1L)).thenReturn(List.of(existing));
        when(slalomResultRepository.save(any(SlalomResult.class))).thenReturn(saved);

        SlalomResultRequest request = new SlalomResultRequest();
        request.setRegistrationId(1L);
        request.setTime(new BigDecimal("54.222"));
        request.setFinished(true);

        mockMvc.perform(post("/api/slalom-results/second-run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.secondRunTime").value(54.222))
                .andExpect(jsonPath("$.secondRunFinished").value(true));
    }

    @Test
    void getSecondRunParticipantsReturnsResponses() throws Exception {
        Competition competition = competition(1L, "Slalom", CompetitionType.SKI_SLALOM, Gender.FEMALE, 18, null, null);
        Athlete athlete = athlete(1L, "Anna", country(1L, "Norway"), Gender.FEMALE, LocalDate.of(2001, 1, 1));
        CompetitionRegistration registration = registration(1L, athlete, competition);
        SlalomResult result = new SlalomResult(registration);
        result.setFirstRunTime(new BigDecimal("55.123"));
        result.setFirstRunFinished(true);
        when(slalomService.getSecondRunParticipants(1L)).thenReturn(List.of(result));
        when(slalomService.toResponse(result)).thenReturn(new SlalomResultResponse(
                1L,
                "Anna",
                "Norway",
                new BigDecimal("55.123"),
                true,
                null,
                false
        ));

        mockMvc.perform(get("/api/slalom-results/second-run/participants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].athleteName").value("Anna"));
    }

    @Test
    void getSecondRunStartOrderReturnsResponses() throws Exception {
        Competition competition = competition(1L, "Slalom", CompetitionType.SKI_SLALOM, Gender.FEMALE, 18, null, null);
        Athlete athlete = athlete(1L, "Anna", country(1L, "Norway"), Gender.FEMALE, LocalDate.of(2001, 1, 1));
        CompetitionRegistration registration = registration(1L, athlete, competition);
        SlalomResult result = new SlalomResult(registration);
        result.setFirstRunTime(new BigDecimal("55.123"));
        result.setFirstRunFinished(true);
        when(slalomService.getSecondRunStartOrder(1L)).thenReturn(List.of(result));
        when(slalomService.toResponse(result)).thenReturn(new SlalomResultResponse(
                1L,
                "Anna",
                "Norway",
                new BigDecimal("55.123"),
                true,
                null,
                false
        ));

        mockMvc.perform(get("/api/slalom-results/second-run/start-order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].athleteName").value("Anna"));
    }

    @Test
    void getSlalomRankingReturnsRanking() throws Exception {
        when(slalomService.getRanking(1L)).thenReturn(List.of(
                new SlalomRankingResponse(1, "Anna", "Norway", new BigDecimal("55.123"), new BigDecimal("54.222"), new BigDecimal("109.345"))
        ));

        mockMvc.perform(get("/api/slalom-results/ranking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].position").value(1))
                .andExpect(jsonPath("$[0].finalTime").value(109.345));
    }

    @Test
    void getSlalomMedalsReturnsTopThreeMedals() throws Exception {
        when(slalomService.getRanking(1L)).thenReturn(List.of(
                new SlalomRankingResponse(1, "Anna", "Norway", new BigDecimal("55.123"), new BigDecimal("54.222"), new BigDecimal("109.345")),
                new SlalomRankingResponse(2, "Beth", "Sweden", new BigDecimal("56.000"), new BigDecimal("55.000"), new BigDecimal("111.000")),
                new SlalomRankingResponse(3, "Cara", "Finland", new BigDecimal("57.000"), new BigDecimal("56.000"), new BigDecimal("113.000")),
                new SlalomRankingResponse(4, "Dana", "Canada", new BigDecimal("58.000"), new BigDecimal("57.000"), new BigDecimal("115.000"))
        ));
        when(statisticsService.getMedalForPosition(1)).thenReturn(Medal.GOLD);
        when(statisticsService.getMedalForPosition(2)).thenReturn(Medal.SILVER);
        when(statisticsService.getMedalForPosition(3)).thenReturn(Medal.BRONZE);

        mockMvc.perform(get("/api/olympics/medals/slalom/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].medal").value("GOLD"));
    }

    @Test
    void getBiathlonMedalsReturnsTopThreeMedals() throws Exception {
        when(biathlonService.getRankingResponse(1L)).thenReturn(List.of(
                new BiathlonRankingResponse(1, "Erik", "Norway", new BigDecimal("123.456"), 1, new BigDecimal("0.5"), new BigDecimal("123.956")),
                new BiathlonRankingResponse(2, "Finn", "Sweden", new BigDecimal("124.000"), 2, new BigDecimal("1.0"), new BigDecimal("126.000")),
                new BiathlonRankingResponse(3, "Gus", "Finland", new BigDecimal("125.000"), 3, new BigDecimal("1.5"), new BigDecimal("129.500"))
        ));
        when(statisticsService.getMedalForPosition(1)).thenReturn(Medal.GOLD);
        when(statisticsService.getMedalForPosition(2)).thenReturn(Medal.SILVER);
        when(statisticsService.getMedalForPosition(3)).thenReturn(Medal.BRONZE);

        mockMvc.perform(get("/api/olympics/medals/biathlon/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].athleteName").value("Erik"));
    }

    @Test
    void getCountryMedalsAggregatesResults() throws Exception {
        Competition slalomCompetition = competition(1L, "Slalom", CompetitionType.SKI_SLALOM, Gender.FEMALE, 18, null, null);
        Competition biathlonCompetition = competition(2L, "Biathlon", CompetitionType.BIATHLON, Gender.MALE, 18, 5, 2);
        when(competitionRepository.findAll()).thenReturn(List.of(slalomCompetition, biathlonCompetition));
        when(slalomService.getRanking(1L)).thenReturn(List.of(
                new SlalomRankingResponse(1, "Anna", "Norway", new BigDecimal("55.123"), new BigDecimal("54.222"), new BigDecimal("109.345"))
        ));
        when(biathlonService.getRankingResponse(2L)).thenReturn(List.of(
                new BiathlonRankingResponse(1, "Erik", "Norway", new BigDecimal("123.456"), 1, new BigDecimal("0.5"), new BigDecimal("123.956"))
        ));
        when(statisticsService.getMedalForPosition(1)).thenReturn(Medal.GOLD);
        when(statisticsService.calculateCountryMedals(anyList())).thenReturn(List.of(
                new CountryMedalResponse("Norway", 2, 0, 0)
        ));

        mockMvc.perform(get("/api/olympics/medals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].country").value("Norway"))
                .andExpect(jsonPath("$[0].gold").value(2));
    }

    @Test
    void getStatisticsReturnsSummary() throws Exception {
        Competition slalomCompetition = competition(1L, "Slalom", CompetitionType.SKI_SLALOM, Gender.FEMALE, 18, null, null);
        Competition biathlonCompetition = competition(2L, "Biathlon", CompetitionType.BIATHLON, Gender.MALE, 18, 5, 2);
        when(competitionRepository.findAll()).thenReturn(List.of(slalomCompetition, biathlonCompetition));
        when(slalomService.getRanking(1L)).thenReturn(List.of(
                new SlalomRankingResponse(1, "Anna", "Norway", new BigDecimal("55.123"), new BigDecimal("54.222"), new BigDecimal("109.345"))
        ));
        when(biathlonService.getRankingResponse(2L)).thenReturn(List.of(
                new BiathlonRankingResponse(1, "Erik", "Norway", new BigDecimal("123.456"), 1, new BigDecimal("0.5"), new BigDecimal("123.956"))
        ));
        when(statisticsService.getMedalForPosition(1)).thenReturn(Medal.GOLD);
        when(statisticsService.getStatistics(anyList())).thenReturn(new OlympicStatisticsResponse(
                23.5,
                new OlympicStatisticsResponse.AthleteAgeResponse("Anna", 19),
                new OlympicStatisticsResponse.AthleteAgeResponse("Erik", 25)
        ));

        mockMvc.perform(get("/api/olympics/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageParticipantAge").value(23.5))
                .andExpect(jsonPath("$.youngestMedalist.athleteName").value("Anna"))
                .andExpect(jsonPath("$.oldestMedalist.age").value(25));
    }

    private String asJson(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private Country country(Long id, String name) {
        Country country = new Country(name);
        ReflectionTestUtils.setField(country, "id", id);
        return country;
    }

    private Athlete athlete(
            Long id,
            String name,
            Country country,
            Gender gender,
            LocalDate dateOfBirth
    ) {
        Athlete athlete = new Athlete(name, country, gender, dateOfBirth);
        ReflectionTestUtils.setField(athlete, "id", id);
        return athlete;
    }

    private Competition competition(
            Long id,
            String name,
            CompetitionType type,
            Gender gender,
            int minimumAge,
            Integer numberOfLaps,
            Integer shootingAfterLaps
    ) {
        Competition competition = new Competition(
                name,
                type,
                gender,
                minimumAge,
                numberOfLaps,
                shootingAfterLaps
        );
        ReflectionTestUtils.setField(competition, "id", id);
        return competition;
    }

    private CompetitionRegistration registration(
            Long id,
            Athlete athlete,
            Competition competition
    ) {
        CompetitionRegistration registration = new CompetitionRegistration(athlete, competition);
        ReflectionTestUtils.setField(registration, "id", id);
        return registration;
    }

    private User sampleUser(
            Long id,
            String username,
            String password,
            Role role,
            Athlete athlete
    ) {
        User user = new User(username, password, role);
        user.setAthlete(athlete);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private RequestPostProcessor authentication(String username, String... authorities) {
        return request -> {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    "N/A",
                    java.util.Arrays.stream(authorities)
                            .map(SimpleGrantedAuthority::new)
                            .toList()
            );
            request.setUserPrincipal(authentication);
            request.setRemoteUser(username);
            return request;
        };
    }
}






