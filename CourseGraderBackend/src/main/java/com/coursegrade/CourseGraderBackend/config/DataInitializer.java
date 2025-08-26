package com.coursegrade.CourseGraderBackend.config;

import com.coursegrade.CourseGraderBackend.repository.CollegeRepository;
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
    private final CollegeRepository collegeRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (courseService.getAllCourses().size() < 7000 || collegeRepository.count() == 0L) {
            System.out.println("Missing data. Starting scraping...");
            webScraperService.wrapperScrape();
            System.out.println("Data initialization completed.");
        } else {
            System.out.println("Courses and colleges already exist in database. Skipping scraping.");
        }
    }
}