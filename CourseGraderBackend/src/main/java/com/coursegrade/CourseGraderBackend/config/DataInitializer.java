package com.coursegrade.CourseGraderBackend.config;

import com.coursegrade.CourseGraderBackend.service.WebScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final WebScraperService webScraperService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Starting course data initialization...");
        webScraperService.wrapperScrape();
        System.out.println("Data initialization completed.");
    }
}