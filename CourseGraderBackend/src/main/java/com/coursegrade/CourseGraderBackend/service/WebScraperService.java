package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.model.College;
import com.coursegrade.CourseGraderBackend.repository.CollegeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WebScraperService {

    private final CourseService courseService;
    private final CollegeRepository collegeRepository;

    public void wrapperScrape() {
        scrapeMajors();
        List<String> courseUrls = new ArrayList<>();
        courseNames(courseUrls, "https://www.bu.edu/academics/cas/courses/",  "CAS"); // For College of Arts & Sciences
        courseNames(courseUrls, "https://www.bu.edu/academics/khc/courses/", "KHC"); // For Arvind & Chandan Nandlal Kilachand Honors College
        courseNames(courseUrls, "https://www.bu.edu/academics/hub/courses/",  "HUB"); // For BU Hub
        courseNames(courseUrls, "https://www.bu.edu/academics/camed/courses/", "MED"); // For Chobanian & Avedisian School of Medicine
        courseNames(courseUrls, "https://www.bu.edu/academics/com/courses/", "COM"); // For College of Communications
        courseNames(courseUrls, "https://www.bu.edu/academics/eng/courses/", "ENG"); // For College of Engineering
        courseNames(courseUrls, "https://www.bu.edu/academics/cfa/courses/", "CFA"); // For College of Fine Arts
        courseNames(courseUrls, "https://www.bu.edu/academics/cgs/courses/", "CGS"); // For College of General Studies
        courseNames(courseUrls, "https://www.bu.edu/academics/cds/courses/", "CDS"); // For Faculty of Computing & Data Sciences
        courseNames(courseUrls, "https://www.bu.edu/academics/gms/courses/", "GMS"); // For Graduate Medical Sciences
        courseNames(courseUrls, "https://www.bu.edu/academics/grs/courses/", "CAS"); // For Graduate School of Arts & Sciences
        courseNames(courseUrls, "https://www.bu.edu/academics/sdm/courses/", "SDM"); // For Henry M. Goldman School of Dental Medicine
        courseNames(courseUrls, "https://www.bu.edu/academics/met/courses/", "GMS"); // For Metropolitan College & Extended Education
        courseNames(courseUrls, "https://www.bu.edu/academics/questrom/courses/", "QST"); // For Questrom School of Business
        courseNames(courseUrls, "https://www.bu.edu/academics/sar/courses/", "SAR"); // For Sargent College of Health & Rehabilitation Sciences
        courseNames(courseUrls, "https://www.bu.edu/academics/sha/courses/", "SHA"); // For School of Hospitality Administration
        courseNames(courseUrls, "https://www.bu.edu/academics/law/courses/", "LAW"); // For School of Law
        courseNames(courseUrls, "https://www.bu.edu/academics/sph/courses/", "SPH"); // For School of Public Health
        courseNames(courseUrls, "https://www.bu.edu/academics/ssw/courses/", "SSW"); // For School of Social Work
        courseNames(courseUrls, "https://www.bu.edu/academics/sth/courses/", "STH"); // For School of Theology
        courseNames(courseUrls, "https://www.bu.edu/academics/wheelock/courses/", "WED"); // For Wheelock College of Education & Human Development */
        courseHubsAndDescription(courseUrls);
    }

    @Transactional
    public void scrapeMajors() {
        try {
            Document doc = Jsoup.connect("https://www.bu.edu/admissions/why-bu/academics/majors/")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(120000)
                    .get(); // 2 minute timeout.get();
            Element majorsListContainer = doc.selectFirst("div.majors-list");
            if (majorsListContainer == null) {
                System.out.println("Could not find majors-list container");
                return;
            }
            Elements majorListItems = majorsListContainer.select("div.major-list-item");
            System.out.println("Number of majors: " + majorListItems.size());
            for (Element majorItem : majorListItems) {
                addMajor(majorItem);
            }
        } catch (IOException e) {
            System.out.println("Error connecting: " + e.getMessage());
        }
    }

    @Transactional
    public void addMajor(Element majorElement) {
        Element majorNameElement = majorElement.selectFirst("div.major-name");
        if (majorNameElement == null) return;
        String majorName = majorNameElement.text();
        System.out.println(majorName);
        Elements majorCollegeElements = majorElement.select("div.major-college");
        for (Element majorCollege : majorCollegeElements) {
            String collegeName = majorCollege.text();
            College college = getCollege(collegeName);
            college.getMajors().add(majorName);
            collegeRepository.save(college);
            System.out.println("College: " + collegeName + " Major: " + majorName);
        }
    }

    @Transactional
    public College getCollege(String collegeName) {
        Optional<College> college = collegeRepository.findById(collegeName);
        if (!college.isPresent()) {
            College newCollege = new College();
            newCollege.setMajors(new HashSet<>());
            newCollege.getMajors().add("Undecided");
            newCollege.setFullName(collegeName);
            collegeRepository.save(newCollege);
            return newCollege;
        }
        else return college.get();
    }

    public void courseNames(List<String> courseUrls, String baseUrl, String college) {
        int numPages = getPageCount(baseUrl);
        System.out.println("Number of pages: " + numPages);
        for (int i = 1; i <= numPages; i++) { // replace with numPages when done testing
            try {
                Document doc = Jsoup.connect(baseUrl + Integer.toString(i))
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(120000)
                        .get(); // 2 minute timeout.get();
                doc.select("a").forEach(element -> {
                    String title = element.text();
                    String href = element.attr("href");
                    if ((title.contains(college) || title.contains("MET")) && href.contains("/courses/")) {
                        String fullUrl = "https://www.bu.edu" + href;
                        courseUrls.add(fullUrl);
                        System.out.println(title);
                        courseDetailsSave(title, fullUrl);
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

    public int getPageCount(String baseUrl) {
        try {
            Document doc = Jsoup.connect(baseUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(120000)
                    .get(); // 2 minute timeout.get();
            Elements paginationDiv = doc.select("div.pagination");
            if (paginationDiv.isEmpty()) {
                return 1; // Assume a single page when no div for pagination
            }
            Elements spans = paginationDiv.select("span");
            if (spans.isEmpty()) {
                return 1;
            }
            Element lastSpan = spans.last();
            Elements anchors = lastSpan.select("a");
            if (!anchors.isEmpty()) {
                String pages = anchors.get(0).text();
                return Integer.parseInt(pages);
            }

        } catch (IOException e) {
            System.out.println("Error connecting: " + e.getMessage());
        }
        return 1;
    }

    public void courseHubsAndDescription(List<String> courseUrls) {
        for (String courseUrl : courseUrls) {
            try {
                Document doc = Jsoup.connect(courseUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(120000)
                        .get(); // 2 minute timeout.get();
                System.out.println("Course: " + courseUrl);
                List<String> hubNames = new ArrayList<>();
                doc.select("ul.cf-hub-offerings li").forEach(element -> {
                    String hub = element.text();
                    System.out.println("Hub: " + hub);
                    hubNames.add(hub);
                });
                String description = null;
                Element courseContentDiv = doc.getElementById("course-content");
                if (courseContentDiv != null) {
                    Element firstParagraph = courseContentDiv.selectFirst("p");
                    if (firstParagraph != null) {
                        description = firstParagraph.text();
                        System.out.println("Description: " + description);
                    }
                }
                courseService.updateCourseWithHubReqsAndDescription(courseUrl, hubNames, description);
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

    public void courseDetailsSave(String title, String url) {
        String[] sub = title.split(" ");
        System.out.println("College: " + sub[0]);
        System.out.println("Department: " + sub[1]);
        String[] preCol = sub[2].split(":");
        System.out.println("Course number: " + preCol[0]);

        int firstColonInd = title.indexOf(": ");
        String courseName = title.substring(firstColonInd + 2);
        System.out.println("Course name: " + courseName);
        System.out.println("College: " + sub[0] + " Department: " + sub[1] + " Course number: " + preCol[0] + " Department: " + courseName);
        courseService.saveScrapedCourse(sub[0], sub[1], preCol[0], courseName, url);
        System.out.println("Saved course in database");
    }
}
