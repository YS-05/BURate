package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.College;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class CollegeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CollegeRepository collegeRepository;

    @Test
    void save_ShouldPersistCollege() {
        // Given
        College college = createTestCollege("College of Arts & Sciences",
                Set.of("Computer Science", "Mathematics", "Physics"));
        // When
        College saved = collegeRepository.save(college);
        // Then
        assertThat(saved.getFullName()).isEqualTo("College of Arts & Sciences");
        assertThat(saved.getMajors()).hasSize(3);
        assertThat(saved.getMajors()).containsExactlyInAnyOrder(
                "Computer Science", "Mathematics", "Physics");
    }

    @Test
    void findById_WhenCollegeExists_ShouldReturnCollege() {
        // Given
        College college = createTestCollege("Engineering",
                Set.of("Electrical Engineering", "Mechanical Engineering"));
        entityManager.persistAndFlush(college);

        // When
        Optional<College> found = collegeRepository.findById("Engineering");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Engineering");
        assertThat(found.get().getMajors()).hasSize(2);
        assertThat(found.get().getMajors()).containsExactlyInAnyOrder(
                "Electrical Engineering", "Mechanical Engineering");
    }

    @Test
    void findById_WhenCollegeDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<College> found = collegeRepository.findById("Nonexistent College");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findById_WithNullId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> collegeRepository.findById(null))
                .isInstanceOf(org.springframework.dao.InvalidDataAccessApiUsageException.class)
                .hasMessageContaining("The given id must not be null");
    }

    @Test
    void findAll_ShouldReturnAllColleges() {
        // Given
        College cas = createTestCollege("CAS", Set.of("Biology", "Chemistry"));
        College eng = createTestCollege("ENG", Set.of("Computer Engineering"));
        College com = createTestCollege("COM", Set.of("Business Administration"));

        entityManager.persistAndFlush(cas);
        entityManager.persistAndFlush(eng);
        entityManager.persistAndFlush(com);

        // When
        var colleges = collegeRepository.findAll();

        // Then
        assertThat(colleges).hasSize(3);
        assertThat(colleges).extracting(College::getFullName)
                .containsExactlyInAnyOrder("CAS", "ENG", "COM");
    }

    @Test
    void existsById_WhenCollegeExists_ShouldReturnTrue() {
        // Given
        College college = createTestCollege("School of Medicine", Set.of("Medicine"));
        entityManager.persistAndFlush(college);

        // When
        boolean exists = collegeRepository.existsById("School of Medicine");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WhenCollegeDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = collegeRepository.existsById("Nonexistent College");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void deleteById_ShouldRemoveCollege() {
        // Given
        College college = createTestCollege("Law School", Set.of("Law"));
        College saved = entityManager.persistAndFlush(college);

        // When
        collegeRepository.deleteById(saved.getFullName());
        entityManager.flush();

        // Then
        Optional<College> found = collegeRepository.findById("Law School");
        assertThat(found).isEmpty();
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        // Given
        College college1 = createTestCollege("College1", Set.of("Major1"));
        College college2 = createTestCollege("College2", Set.of("Major2"));
        entityManager.persistAndFlush(college1);
        entityManager.persistAndFlush(college2);

        // When
        long count = collegeRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
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

    @Test
    void save_CollegeWithSingleMajor_ShouldPersist() {
        // Given
        College college = createTestCollege("Graduate School", Set.of("Philosophy"));

        // When
        College saved = collegeRepository.save(college);

        // Then
        assertThat(saved.getFullName()).isEqualTo("Graduate School");
        assertThat(saved.getMajors()).hasSize(1);
        assertThat(saved.getMajors()).contains("Philosophy");
    }

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
    void findAll_WhenNoColleges_ShouldReturnEmptyList() {
        // When
        var colleges = collegeRepository.findAll();

        // Then
        assertThat(colleges).isEmpty();
    }

    // Helper method to create test colleges
    private College createTestCollege(String fullName, Set<String> majors) {
        College college = new College();
        college.setFullName(fullName);
        college.setMajors(new HashSet<>(majors)); // Create mutable HashSet
        return college;
    }
}