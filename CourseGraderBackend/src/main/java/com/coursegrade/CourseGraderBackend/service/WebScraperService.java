package com.coursegrade.CourseGraderBackend.service;

import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WebScraperService {

    @PostConstruct
    public void casCourseNames() {
        List<String> courseUrls = new ArrayList<>();
        int numPages = 153;
        String baseUrl = "https://www.bu.edu/academics/cas/courses/";
        for (int i = 1; i <= 3; i++) { // Replace with numPages when not testing
            try {
                Document doc = Jsoup.connect(baseUrl + Integer.toString(i))
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(10000)
                        .get(); // 10 second timeout.get();
                doc.select("a").forEach(element -> {
                    String title = element.text();
                    String href = element.attr("href");
                    if (title.contains("CAS ") && href.contains("/courses/")) {
                        courseUrls.add("https://www.bu.edu" + href);
                        System.out.println(title);
                    }
                });
            } catch (IOException e) {
                System.out.println("Error connecting: " + e.getMessage());
            }
            System.out.println("Scraping page " + i + " of " + numPages);
            threadSleeper();
        }
        System.out.println("All course names scraped");
        System.out.println("Number of courses: " +courseUrls.size());
        List<String> testUrls = courseUrls.subList(0, Math.min(5, courseUrls.size()));
        System.out.println("Testing with first " + testUrls.size() + " courses...");
        casCourseHubsFulfilled(testUrls);
    }

    public void casCourseHubsFulfilled(List<String> courseUrls) {
        for (String courseUrl : courseUrls) {
            try {
                Document doc = Jsoup.connect(courseUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(10000)
                        .get(); // 10 second timeout.get();
                System.out.println("Course: " + courseUrl);
                doc.select("ul.cf-hub-offerings li").forEach(element -> {
                    String hub = element.text();
                    System.out.println("Hub: " + hub);
                });
            } catch (IOException e) {
                System.out.println("Error connecting: " + e.getMessage());
            }
            threadSleeper();
        }
        System.out.println("All course hubs scraped");
    }

    public void threadSleeper() {
        try {
            Thread.sleep(500); // .5 second delay to prevent overload
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted: " + e.getMessage());
        }
    }

}
