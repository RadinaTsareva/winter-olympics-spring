package com.example.winter_olympics.controller;

import com.example.winter_olympics.dto.TestDataResponse;
import com.example.winter_olympics.service.TestDataService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminTestDataController {

    private final TestDataService testDataService;

    public AdminTestDataController(TestDataService testDataService) {
        this.testDataService = testDataService;
    }

    @PostMapping("/test-data")
    public TestDataResponse createTestData() {
        return testDataService.createDemoData();
    }
}
