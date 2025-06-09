package com.coursegrade.CourseGraderBackend.config;

import com.coursegrade.CourseGraderBackend.repository.CourseRepository;
import com.coursegrade.CourseGraderBackend.service.CourseService;
import com.coursegrade.CourseGraderBackend.service.WebScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final WebScraperService webScraperService;
    private final CourseService courseService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (courseService.getAllCourses().isEmpty()) {
            System.out.println("No courses found. Starting scraping...");
            webScraperService.wrapperScrape();
            System.out.println("Data initialization completed.");
        } else {
            System.out.println("Courses already exist in database. Skipping scraping.");
        }
    }
}