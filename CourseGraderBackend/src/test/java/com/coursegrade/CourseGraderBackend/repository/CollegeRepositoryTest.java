package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.College;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CollegeRepositoryTest {

    @Autowired
    private CollegeRepository collegeRepository;

    @Test
    void save_UpdateExistingCollege_ShouldUpdateMajors() {
        // Given - Save initial college
        College college = createTestCollege("Business School", new HashSet<>(Set.of("Accounting")));
        College saved = collegeRepository.save(college);

        // When - Update with new majors
        saved.setMajors(new HashSet<>(Set.of("Accounting", "Finance", "Marketing")));
        College updated = collegeRepository.save(saved);

        // Then
        assertThat(updated.getFullName()).isEqualTo("Business School");
        assertThat(updated.getMajors()).hasSize(3);
        assertThat(updated.getMajors()).containsExactlyInAnyOrder(
                "Accounting", "Finance", "Marketing");
    }

    @Test
    void save_CollegeWithEmptyMajors_ShouldPersist() {
        // Given
        College college = createTestCollege("New College", Set.of());

        // When
        College saved = collegeRepository.save(college);

        // Then
        assertThat(saved.getFullName()).isEqualTo("New College");
        assertThat(saved.getMajors()).isEmpty();
    }

    private College createTestCollege(String fullName, Set<String> majors) {
        College college = new College();
        college.setFullName(fullName);
        college.setMajors(new HashSet<>(majors));
        return college;
    }
}