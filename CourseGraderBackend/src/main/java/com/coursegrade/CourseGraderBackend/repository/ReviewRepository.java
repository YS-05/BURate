package com.coursegrade.CourseGraderBackend.repository;

import com.coursegrade.CourseGraderBackend.model.Course;
import com.coursegrade.CourseGraderBackend.model.Review;
import com.coursegrade.CourseGraderBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCourse(Course course);
    List<Review> findByUser(User user);
    List<Review> findByCourseOrderByCreatedAtDesc(Course course);
    Optional<Review> findByCourseAndUser(Course course, User user);
    List<Review> findByCourseAndTeacherNameContainingIgnoreCase(Course course, String teacherName);
    List<Review> findByUserOrderByCreatedAtDesc(User user);
}

