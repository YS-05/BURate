package com.coursegrade.CourseGraderBackend.service;

import com.coursegrade.CourseGraderBackend.model.College;
import com.coursegrade.CourseGraderBackend.repository.CollegeRepository;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebScraperServiceTest {

    @Mock
    private CourseService courseService;

    @Mock
    private CollegeRepository collegeRepository;

    @InjectMocks
    private WebScraperService webScraperService;

    @Test
    void courseDetailsSave_ValidCourseTitle_ShouldParseCorrectly() {
        // Given - A typical course title format
        String courseTitle = "CAS CS 112: Introduction to Computer Science II";
        String courseUrl = "https://www.bu.edu/academics/cas/courses/cas-cs-112/";

        // When
        webScraperService.courseDetailsSave(courseTitle, courseUrl);

        // Then - Should extract and pass correct parts to courseService
        verify(courseService).saveScrapedCourse(
                "CAS",                                    // college
                "CS",                                     // department
                "112",                                    // course number
                "Introduction to Computer Science II",    // course name
                courseUrl                                 // url
        );
    }

    @Test
    void courseDetailsSave_CourseWithComplexName_ShouldParseCorrectly() {
        // Given - Course with complex name containing colons and special chars
        String courseTitle = "ENG ME 305: Thermodynamics: Heat and Work";
        String courseUrl = "https://www.bu.edu/academics/eng/courses/eng-me-305/";

        // When
        webScraperService.courseDetailsSave(courseTitle, courseUrl);

        // Then - Should handle multiple colons correctly (only split on first)
        verify(courseService).saveScrapedCourse(
                "ENG",
                "ME",
                "305",
                "Thermodynamics: Heat and Work",
                courseUrl
        );
    }

    @Test
    void getCollege_ExistingCollege_ShouldReturnExisting() {
        // Given - College already exists
        College existingCollege = new College();
        existingCollege.setFullName("College of Arts & Sciences");
        existingCollege.setMajors(new HashSet<>());

        when(collegeRepository.findById("College of Arts & Sciences"))
                .thenReturn(Optional.of(existingCollege));

        // When
        College result = webScraperService.getCollege("College of Arts & Sciences");

        // Then - Should return existing college without creating new one
        assertThat(result).isEqualTo(existingCollege);
        verify(collegeRepository, never()).save(any(College.class));
    }

    @Test
    void getCollege_NewCollege_ShouldCreateWithDefaults() {
        // Given - College doesn't exist
        when(collegeRepository.findById("New College")).thenReturn(Optional.empty());

        College savedCollege = new College();
        savedCollege.setFullName("New College");
        savedCollege.setMajors(new HashSet<>());
        savedCollege.getMajors().add("Undecided");
        when(collegeRepository.save(any(College.class))).thenReturn(savedCollege);

        // When
        College result = webScraperService.getCollege("New College");

        // Then - Should create new college with "Undecided" major
        assertThat(result.getFullName()).isEqualTo("New College");
        assertThat(result.getMajors()).contains("Undecided");
        verify(collegeRepository).save(any(College.class));
    }

    // MAJOR PROCESSING - Test HTML element processing logic
    @Test
    void addMajor_ValidMajorElement_ShouldAddMajorToColleges() {
        // Given - Mock HTML elements (we can't easily create real Jsoup elements in unit tests)
        Element majorElement = mock(Element.class);
        Element majorNameElement = mock(Element.class);
        Element majorCollegeElement = mock(Element.class);
        Elements majorCollegeElements = mock(Elements.class);

        when(majorElement.selectFirst("div.major-name")).thenReturn(majorNameElement);
        when(majorNameElement.text()).thenReturn("Computer Science");
        when(majorElement.select("div.major-college")).thenReturn(majorCollegeElements);
        when(majorCollegeElements.iterator()).thenReturn(java.util.List.of(majorCollegeElement).iterator());
        when(majorCollegeElement.text()).thenReturn("College of Arts & Sciences");

        // Mock college creation
        College mockCollege = new College();
        mockCollege.setMajors(new HashSet<>());
        when(collegeRepository.findById("College of Arts & Sciences")).thenReturn(Optional.of(mockCollege));

        // When
        webScraperService.addMajor(majorElement);

        // Then - Should add major to college
        assertThat(mockCollege.getMajors()).contains("Computer Science");
        verify(collegeRepository).save(mockCollege);
    }

    @Test
    void addMajor_MissingMajorName_ShouldHandleGracefully() {
        // Given - HTML element without major name
        Element majorElement = mock(Element.class);
        when(majorElement.selectFirst("div.major-name")).thenReturn(null);

        // When
        webScraperService.addMajor(majorElement);

        // Then - Should return early without processing
        verify(collegeRepository, never()).save(any(College.class));
    }
}