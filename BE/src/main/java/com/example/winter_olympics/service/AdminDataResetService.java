package com.example.winter_olympics.service;

import com.example.winter_olympics.dto.AdminDataResetResponse;
import com.example.winter_olympics.entity.Role;
import com.example.winter_olympics.entity.User;
import com.example.winter_olympics.exception.BadRequestException;
import com.example.winter_olympics.exception.ForbiddenException;
import com.example.winter_olympics.exception.NotFoundException;
import com.example.winter_olympics.repository.AthleteRepository;
import com.example.winter_olympics.repository.BiathlonResultRepository;
import com.example.winter_olympics.repository.CompetitionRegistrationRepository;
import com.example.winter_olympics.repository.CompetitionRepository;
import com.example.winter_olympics.repository.CountryRepository;
import com.example.winter_olympics.repository.SlalomResultRepository;
import com.example.winter_olympics.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminDataResetService {

    public static final String REQUIRED_CONFIRMATION = "DELETE ALL OLYMPICS DATA";

    private final UserRepository userRepository;
    private final SlalomResultRepository slalomResultRepository;
    private final BiathlonResultRepository biathlonResultRepository;
    private final CompetitionRegistrationRepository registrationRepository;
    private final AthleteRepository athleteRepository;
    private final CompetitionRepository competitionRepository;
    private final CountryRepository countryRepository;

    public AdminDataResetService(
            UserRepository userRepository,
            SlalomResultRepository slalomResultRepository,
            BiathlonResultRepository biathlonResultRepository,
            CompetitionRegistrationRepository registrationRepository,
            AthleteRepository athleteRepository,
            CompetitionRepository competitionRepository,
            CountryRepository countryRepository
    ) {
        this.userRepository = userRepository;
        this.slalomResultRepository = slalomResultRepository;
        this.biathlonResultRepository = biathlonResultRepository;
        this.registrationRepository = registrationRepository;
        this.athleteRepository = athleteRepository;
        this.competitionRepository = competitionRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional
    public AdminDataResetResponse resetAllData(String authenticatedUsername, String confirmation) {
        if (!REQUIRED_CONFIRMATION.equals(confirmation)) {
            throw new BadRequestException("Type the exact confirmation phrase to reset application data");
        }

        User admin = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new NotFoundException("Authenticated admin account not found"));
        if (admin.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only an administrator can reset application data");
        }

        // Remove the admin's optional athlete association before deleting athlete rows.
        admin.setAthlete(null);
        userRepository.saveAndFlush(admin);
        userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(admin.getId()))
                .forEach(userRepository::delete);
        userRepository.flush();

        // Delete dependent rows before their referenced registrations and parent data.
        slalomResultRepository.deleteAllInBatch();
        biathlonResultRepository.deleteAllInBatch();
        registrationRepository.deleteAllInBatch();
        athleteRepository.deleteAllInBatch();
        competitionRepository.deleteAllInBatch();
        countryRepository.deleteAllInBatch();

        return new AdminDataResetResponse(
                "All application data was deleted. The signed-in administrator account was preserved.",
                admin.getUsername()
        );
    }
}
