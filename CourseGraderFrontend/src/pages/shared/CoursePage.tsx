import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { CourseDTO } from "../../auth/AuthDTOs";
import { useAuth } from "../../auth/AuthProvider";
import { fetchCourseById } from "../../api/axios";
import Spinner from "../../components/Spinner";
import ErrorDisplay from "../../components/ErrorDisplay";
import CourseHeader from "../../components/CourseHeader";
import RatingsOverview from "../../components/RatingsOverview";
import CourseDescription from "../../components/CourseDescription";
import HubDisplay from "../../components/HubDisplay";
import ReviewCard from "../../components/ReviewCard";
import CourseAction from "../../components/CourseAction";

const CoursePage = () => {
  const { courseId } = useParams<{ courseId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [course, setCourse] = useState<CourseDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>("");

  useEffect(() => {
    const loadCourse = async () => {
      if (!courseId) {
        setError("No course Id provided");
        setLoading(false);
        return;
      }
      try {
        setLoading(true);
        const courseData = await fetchCourseById(courseId);
        setCourse(courseData);
      } catch (err: any) {
        if (err.response?.status === 404) {
          setError("Course not found");
        } else {
          setError("Failed to load course details");
        }
      } finally {
        setLoading(false);
      }
    };
    loadCourse();
  }, [courseId]);

  if (loading) {
    return <Spinner />;
  }

  if (error) {
    return (
      <>
        <ErrorDisplay error={error} />
        <button
          className="btn btn-outline-danger"
          onClick={() => navigate("/search")}
        >
          Back to Search
        </button>
      </>
    );
  }

  return (
    <div
      className="min-vh-100"
      style={{ backgroundColor: "#f5f5f5", padding: "30px" }}
    >
      <div className="container">
        <div className="mb-4">
          <button
            className="btn btn-outline-secondary"
            onClick={() => navigate(-1)}
          >
            ← Back
          </button>
        </div>
        <CourseHeader
          courseCode={course?.courseCode}
          courseCollege={course?.college}
          courseDepartment={course?.department}
          numReviews={course?.numReviews}
          noPreReqs={course?.noPreReqs}
          courseTitle={course?.title}
        />
        <div className="row g-4">
          <div className="col-lg-8">
            <RatingsOverview
              overallRating={course?.averageOverallRating}
              usefulRating={course?.averageUsefulnessRating}
              difficultRating={course?.averageDifficultyRating}
              workloadRating={course?.averageWorkloadRating}
              interestRating={course?.averageInterestRating}
              teacherRating={course?.averageTeacherRating}
            />
            <CourseDescription description={course?.description} />
            <HubDisplay hubs={course?.hubRequirements} />
            <ReviewCard reviews={course?.courseReviews} id={courseId} />
          </div>
          <div className="col-lg-4">
            <CourseAction id={courseId} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default CoursePage;
