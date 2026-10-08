package com.example.winter_olympics.service;

import com.example.winter_olympics.dto.TestDataResponse;
import com.example.winter_olympics.entity.*;
import com.example.winter_olympics.repository.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.winter_olympics.exception.BadRequestException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class TestDataService {

    private final CountryRepository countryRepository;
    private final AthleteRepository athleteRepository;
    private final CompetitionRepository competitionRepository;
    private final CompetitionRegistrationRepository registrationRepository;
    private final SlalomResultRepository slalomResultRepository;
    private final BiathlonResultRepository biathlonResultRepository;
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String demoAthletePassword;

    public TestDataService(
            CountryRepository countryRepository,
            AthleteRepository athleteRepository,
            CompetitionRepository competitionRepository,
            CompetitionRegistrationRepository registrationRepository,
            SlalomResultRepository slalomResultRepository,
            BiathlonResultRepository biathlonResultRepository,
            JdbcTemplate jdbcTemplate,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.demo.athlete-password:}") String demoAthletePassword
    ) {
        this.countryRepository = countryRepository;
        this.athleteRepository = athleteRepository;
        this.competitionRepository = competitionRepository;
        this.registrationRepository = registrationRepository;
        this.slalomResultRepository = slalomResultRepository;
        this.biathlonResultRepository = biathlonResultRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.demoAthletePassword = demoAthletePassword;
    }

    @Transactional
    public synchronized TestDataResponse createDemoData() {
        if (demoAthletePassword == null || demoAthletePassword.isBlank()) {
            throw new BadRequestException(
                    "Set DEMO_ATHLETE_PASSWORD on the backend before creating test data"
            );
        }
        // A transaction-scoped PostgreSQL advisory lock prevents concurrent app instances
        // from racing the stable-identity lookups and inserting duplicate demo records.
        jdbcTemplate.execute("SELECT pg_advisory_xact_lock(731942018)");
        Counts counts = new Counts();
        Map<String, Country> countries = new HashMap<>();
        for (String name : new String[]{"Bulgaria", "Norway", "Sweden", "Germany"}) {
            Seed<Country> seed = seedCountry(name);
            countries.put(name, seed.value());
            if (seed.created()) counts.countriesCreated++; else counts.countriesReused++;
        }

        Map<String, Athlete> athletes = new HashMap<>();
        AthleteSpec[] specs = {
                new AthleteSpec("Ivan Ivanov", "Bulgaria", Gender.MALE, "2000-05-15"),
                new AthleteSpec("Georgi Georgiev", "Bulgaria", Gender.MALE, "1999-03-20"),
                new AthleteSpec("Maria Petrova", "Bulgaria", Gender.FEMALE, "1998-11-10"),
                new AthleteSpec("Anna Schmidt", "Germany", Gender.FEMALE, "2001-02-12"),
                new AthleteSpec("Erik Hansen", "Norway", Gender.MALE, "1997-08-25"),
                new AthleteSpec("Ingrid Larsen", "Norway", Gender.FEMALE, "2000-06-18"),
                new AthleteSpec("Sofia Berg", "Sweden", Gender.FEMALE, "1996-01-30"),
                new AthleteSpec("Lukas Weber", "Germany", Gender.MALE, "1995-09-14")
        };
        for (AthleteSpec spec : specs) {
            Seed<Athlete> seed = seedAthlete(spec, countries.get(spec.country()));
            athletes.put(spec.name(), seed.value());
            if (seed.created()) counts.athletesCreated++; else counts.athletesReused++;
        }

        for (AthleteSpec spec : specs) {
            Seed<User> seed = seedDemoUser(spec, athletes.get(spec.name()));
            if (seed.created()) counts.usersCreated++; else counts.usersReused++;
            counts.demoUsernames.add(seed.value().getUsername());
        }

        Map<String, Competition> competitions = new HashMap<>();
        CompetitionSpec[] competitionSpecs = {
                new CompetitionSpec("Men's Ski Slalom", CompetitionType.SKI_SLALOM, Gender.MALE, null, null),
                new CompetitionSpec("Women's Ski Slalom", CompetitionType.SKI_SLALOM, Gender.FEMALE, null, null),
                new CompetitionSpec("Men's Biathlon", CompetitionType.BIATHLON, Gender.MALE, 4, 2),
                new CompetitionSpec("Women's Biathlon", CompetitionType.BIATHLON, Gender.FEMALE, 4, 2)
        };
        for (CompetitionSpec spec : competitionSpecs) {
            Seed<Competition> seed = seedCompetition(spec);
            competitions.put(spec.name(), seed.value());
            if (seed.created()) counts.competitionsCreated++; else counts.competitionsReused++;
        }

        Map<String, CompetitionRegistration> registrations = new HashMap<>();
        String[] men = {"Ivan Ivanov", "Georgi Georgiev", "Erik Hansen", "Lukas Weber"};
        String[] women = {"Maria Petrova", "Anna Schmidt", "Ingrid Larsen", "Sofia Berg"};
        String[][] registrationGroups = {
                {"Men's Ski Slalom", "men"}, {"Women's Ski Slalom", "women"},
                {"Men's Biathlon", "men"}, {"Women's Biathlon", "women"}
        };
        for (String[] group : registrationGroups) {
            String competitionName = group[0];
            String[] participants = group[1].equals("men") ? men : women;
            for (String athleteName : participants) {
                Seed<CompetitionRegistration> seed = seedRegistration(
                        athletes.get(athleteName), competitions.get(competitionName));
                registrations.put(registrationKey(competitionName, athleteName), seed.value());
                if (seed.created()) counts.registrationsCreated++; else counts.registrationsReused++;
            }
        }

        seedSlalom(registrations, "Men's Ski Slalom", new SlalomSeed[]{
                new SlalomSeed("Ivan Ivanov", "52.341", true, "51.827", true),
                new SlalomSeed("Georgi Georgiev", "51.900", true, "53.500", true),
                new SlalomSeed("Erik Hansen", "53.200", true, "52.900", true),
                new SlalomSeed("Lukas Weber", "55.100", false, null, false)
        }, counts);
        seedSlalom(registrations, "Women's Ski Slalom", new SlalomSeed[]{
                new SlalomSeed("Maria Petrova", "54.100", true, "53.400", true),
                new SlalomSeed("Anna Schmidt", "53.800", true, "53.600", true),
                new SlalomSeed("Ingrid Larsen", "54.300", true, "53.800", true),
                new SlalomSeed("Sofia Berg", "56.200", false, null, false)
        }, counts);

        seedBiathlon(registrations, "Men's Biathlon", new BiathlonSeed[]{
                new BiathlonSeed("Ivan Ivanov", "1450.250", 2, true),
                new BiathlonSeed("Georgi Georgiev", "1500.500", 0, true),
                new BiathlonSeed("Erik Hansen", "1480.000", 1, true),
                new BiathlonSeed("Lukas Weber", "1600.000", 3, false)
        }, counts);
        seedBiathlon(registrations, "Women's Biathlon", new BiathlonSeed[]{
                new BiathlonSeed("Maria Petrova", "1470.250", 1, true),
                new BiathlonSeed("Anna Schmidt", "1495.000", 0, true),
                new BiathlonSeed("Ingrid Larsen", "1500.000", 2, true),
                new BiathlonSeed("Sofia Berg", "1600.000", 3, false)
        }, counts);

        String message = counts.totalCreated() == 0
                ? "Existing demo data was reused; no records were created"
                : "Test data created successfully; matching demo records were reused";
        return counts.toResponse(message);
    }

    private Seed<Country> seedCountry(String name) {
        return countryRepository.findByNameIgnoreCase(name)
                .map(country -> new Seed<>(country, false))
                .orElseGet(() -> new Seed<>(countryRepository.save(new Country(name)), true));
    }

    private Seed<Athlete> seedAthlete(AthleteSpec spec, Country country) {
        LocalDate birthDate = LocalDate.parse(spec.dateOfBirth());
        return athleteRepository.findFirstByNameAndCountry_IdAndGenderAndDateOfBirth(
                        spec.name(), country.getId(), spec.gender(), birthDate)
                .map(athlete -> new Seed<>(athlete, false))
                .orElseGet(() -> new Seed<>(athleteRepository.save(new Athlete(
                        spec.name(), country, spec.gender(), birthDate)), true));
    }

    private Seed<Competition> seedCompetition(CompetitionSpec spec) {
        return competitionRepository.findFirstByNameAndTypeAndGender(
                        spec.name(), spec.type(), spec.gender())
                .map(competition -> new Seed<>(competition, false))
                .orElseGet(() -> new Seed<>(competitionRepository.save(new Competition(
                        spec.name(), spec.type(), spec.gender(), 18,
                        spec.numberOfLaps(), spec.shootingAfterLaps())), true));
    }

    private Seed<User> seedDemoUser(AthleteSpec spec, Athlete athlete) {
        String username = "demo." + spec.name().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", ".")
                .replaceAll("^\\.|\\.$", "");

        return userRepository.findByUsername(username)
                .map(user -> {
                    if (user.getRole() != Role.ATHLETE || user.getAthlete() == null
                            || !user.getAthlete().getId().equals(athlete.getId())) {
                        throw new BadRequestException(
                                "Demo username is already associated with another account: " + username
                        );
                    }
                    return new Seed<>(user, false);
                })
                .orElseGet(() -> {
                    User user = new User(username,
                            passwordEncoder.encode(demoAthletePassword), Role.ATHLETE);
                    user.setAthlete(athlete);
                    return new Seed<>(userRepository.save(user), true);
                });
    }

    private Seed<CompetitionRegistration> seedRegistration(
            Athlete athlete, Competition competition) {
        return registrationRepository.findFirstByAthlete_IdAndCompetition_Id(
                        athlete.getId(), competition.getId())
                .map(registration -> new Seed<>(registration, false))
                .orElseGet(() -> new Seed<>(registrationRepository.save(
                        new CompetitionRegistration(athlete, competition)), true));
    }

    private void seedSlalom(
            Map<String, CompetitionRegistration> registrations,
            String competitionName,
            SlalomSeed[] results,
            Counts counts
    ) {
        for (SlalomSeed data : results) {
            CompetitionRegistration registration = registrations.get(
                    registrationKey(competitionName, data.athleteName()));
            var existing = slalomResultRepository.findFirstByRegistration_Id(registration.getId());
            if (existing.isPresent()) {
                counts.slalomResultsReused++;
                continue;
            }
            SlalomResult result = new SlalomResult(registration);
            result.setFirstRunTime(new BigDecimal(data.firstRunTime()));
            result.setFirstRunFinished(data.firstRunFinished());
            result.setSecondRunTime(data.secondRunTime() == null
                    ? null : new BigDecimal(data.secondRunTime()));
            result.setSecondRunFinished(data.secondRunFinished());
            slalomResultRepository.save(result);
            counts.slalomResultsCreated++;
        }
    }

    private void seedBiathlon(
            Map<String, CompetitionRegistration> registrations,
            String competitionName,
            BiathlonSeed[] results,
            Counts counts
    ) {
        for (BiathlonSeed data : results) {
            CompetitionRegistration registration = registrations.get(
                    registrationKey(competitionName, data.athleteName()));
            var existing = biathlonResultRepository.findFirstByRegistration_Id(registration.getId());
            if (existing.isPresent()) {
                counts.biathlonResultsReused++;
                continue;
            }
            BiathlonResult result = new BiathlonResult(registration, new BigDecimal("60.000"));
            result.setSkiTime(new BigDecimal(data.skiTime()));
            result.setMisses(data.misses());
            result.setFinished(data.finished());
            biathlonResultRepository.save(result);
            counts.biathlonResultsCreated++;
        }
    }

    private String registrationKey(String competitionName, String athleteName) {
        return competitionName + "|" + athleteName;
    }

    private record Seed<T>(T value, boolean created) { }
    private record AthleteSpec(String name, String country, Gender gender, String dateOfBirth) { }
    private record CompetitionSpec(String name, CompetitionType type, Gender gender,
                                   Integer numberOfLaps, Integer shootingAfterLaps) { }
    private record SlalomSeed(String athleteName, String firstRunTime, boolean firstRunFinished,
                              String secondRunTime, boolean secondRunFinished) { }
    private record BiathlonSeed(String athleteName, String skiTime, int misses, boolean finished) { }

    private static class Counts {
        int countriesCreated, countriesReused;
        int athletesCreated, athletesReused;
        int usersCreated, usersReused;
        java.util.List<String> demoUsernames = new java.util.ArrayList<>();
        int competitionsCreated, competitionsReused;
        int registrationsCreated, registrationsReused;
        int slalomResultsCreated, slalomResultsReused;
        int biathlonResultsCreated, biathlonResultsReused;

        int totalCreated() {
            return countriesCreated + athletesCreated + usersCreated + competitionsCreated
                    + registrationsCreated + slalomResultsCreated + biathlonResultsCreated;
        }

        TestDataResponse toResponse(String message) {
            return new TestDataResponse(message,
                    countriesCreated, countriesReused,
                    athletesCreated, athletesReused,
                    usersCreated, usersReused, demoUsernames,
                    competitionsCreated, competitionsReused,
                    registrationsCreated, registrationsReused,
                    slalomResultsCreated, slalomResultsReused,
                    biathlonResultsCreated, biathlonResultsReused);
        }
    }
}
