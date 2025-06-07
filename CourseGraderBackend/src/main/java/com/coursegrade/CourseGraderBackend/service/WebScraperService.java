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
    public void wrapperScrape() {
        List<String> courseUrls = new ArrayList<>();

        courseNames(courseUrls, "https://www.bu.edu/academics/cas/courses/", 153, "CAS"); // For College of Arts & Sciences
        courseNames(courseUrls, "https://www.bu.edu/academics/khc/courses/", 3, "KHC"); // For Arvind & Chandan Nandlal Kilachand Honors College
        courseNames(courseUrls, "https://www.bu.edu/academics/hub/courses/", 1, "HUB"); // For BU Hub
        courseNames(courseUrls, "https://www.bu.edu/academics/camed/courses/", 8, "MED"); // For Chobanian & Avedisian School of Medicine
        courseNames(courseUrls, "https://www.bu.edu/academics/com/courses/", 14, "COM"); // For College of Communications
        courseNames(courseUrls, "https://www.bu.edu/academics/eng/courses/", 20, "ENG"); // For College of Engineering
        courseNames(courseUrls, "https://www.bu.edu/academics/cfa/courses/", 39, "CFA"); // For College of Fine Arts
        courseNames(courseUrls, "https://www.bu.edu/academics/cgs/courses/", 2, "CGS"); // For College of General Studies
        courseNames(courseUrls, "https://www.bu.edu/academics/cds/courses/", 3, "CDS"); // For Faculty of Computing & Data Sciences
        courseNames(courseUrls, "https://www.bu.edu/academics/gms/courses/", 23, "GMS"); // For Graduate Medical Sciences
        courseNames(courseUrls, "https://www.bu.edu/academics/grs/courses/", 59, "CAS"); // For Graduate School of Arts & Sciences
        courseNames(courseUrls, "https://www.bu.edu/academics/sdm/courses/", 20, "SDM"); // For Henry M. Goldman School of Dental Medicine
        courseNames(courseUrls, "https://www.bu.edu/academics/met/courses/", 28, "GMS"); // For Metropolitan College & Extended Education
        courseNames(courseUrls, "https://www.bu.edu/academics/questrom/courses/", 21, "QST"); // For Questrom School of Business
        courseNames(courseUrls, "https://www.bu.edu/academics/sar/courses/", 15, "SAR"); // For Sargent College of Health & Rehabilitation Sciences
        courseNames(courseUrls, "https://www.bu.edu/academics/sha/courses/", 4, "SHA"); // For School of Hospitality Administration
        courseNames(courseUrls, "https://www.bu.edu/academics/law/courses/", 17, "LAW"); // For School of Law
        courseNames(courseUrls, "https://www.bu.edu/academics/sph/courses/", 13, "SPH"); // For School of Public Health
        courseNames(courseUrls, "https://www.bu.edu/academics/ssw/courses/", 4, "SSW"); // For School of Social Work
        courseNames(courseUrls, "https://www.bu.edu/academics/sth/courses/", 12, "STH"); // For School of Theology
        courseNames(courseUrls, "https://www.bu.edu/academics/wheelock/courses/", 26, "WED"); // For Wheelock College of Education & Human Development

        courseHubsFulfilled(courseUrls);
    }

    public void courseNames(List<String> courseUrls, String baseUrl, int numPages, String college) {
        for (int i = 1; i <= 1; i++) { // Replace with numPages when not testing
            try {
                Document doc = Jsoup.connect(baseUrl + Integer.toString(i))
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(10000)
                        .get(); // 10 second timeout.get();
                doc.select("a").forEach(element -> {
                    String title = element.text();
                    String href = element.attr("href");
                    if (title.contains(college) && href.contains("/courses/")) {
                        courseUrls.add("https://www.bu.edu" + href);
                        System.out.println(title);
                        courseDetails(title);
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
    }

    public void courseHubsFulfilled(List<String> courseUrls) {
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
            Thread.sleep(1000); // 1-second delay to prevent overload
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted: " + e.getMessage());
        }
    }

    public void courseDetails(String title) {
        String[] sub = title.split(" ");
        System.out.println("College: " + sub[0]);
        System.out.println("Department: " + sub[1]);
        String[] preCol = sub[2].split(":");
        System.out.println("Course number: " + preCol[0]);
        String[] byColon = title.split(": ");
        System.out.println("Course name: " + byColon[1]);
    }

}
