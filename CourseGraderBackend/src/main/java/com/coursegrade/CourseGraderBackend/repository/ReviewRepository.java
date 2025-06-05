package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.Review;
import com.coursegrade.CourseGraderBackend.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository {
    List<Review> findByCourse(Course course);
    List<Review> findByUser(User user);
    List<Review> findByCourseOrderByCreatedAtDesc(Course course);
    Optional<Review> findByCourseAndUser(Course course, User user);

    @Query("SELECT AVG(r.usefulnessRating) FROM Review AS r WHERE r.course = :course")
    Double getAverageUsefulnessRating(@Param("course") Course course);

    @Query("SELECT AVG(r.difficultyRating) FROM Review AS r WHERE r.course = :course")
    Double getAverageDifficultyRating(@Param("course") Course course);

    @Query("SELECT AVG(r.workloadRating) FROM Review AS r WHERE r.course = :course")
    Double getAverageWorkloadRating(@Param("course") Course course);

    @Query("SELECT AVG(r.interestRating) FROM Review AS r WHERE r.course = :course")
    Double getAverageInterestRating(@Param("course") Course course);

    @Query("SELECT AVG(r.teacherRating) FROM Review AS r WHERE r.course = :course")
    Double getAverageTeacherRating(@Param("course") Course course);

    @Query("SELECT AVG((r.usefulnessRating + r.difficultyRating + r.workloadRating + r.interestRating + r.teacherRating) / 5.0) FROM Review r WHERE r.course = :course")
    Double getAverageOverallRating(@Param("course") Course course);

    @Query("SELECT COUNT(r) FROM Review AS r WHERE r.course = :course")
    Long countByCourse(@Param("course") Course course);

    List<Review> findByCourseAndTeacherNameContainingIgnoreCase(Course course, String teacherName);
}

