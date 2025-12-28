import { useParams, useNavigate } from "react-router-dom";
import { CourseDisplayDTO, CourseDTO } from "../../auth/AuthDTOs";
import {
  fetchCourseById,
  fetchTeachersByCourse,
  fetchCourseReviews,
  fetchTeacherScore,
  addCompletedCourse,
  fetchCompletedCourses,
  removeCompletedCourse,
} from "../../api/axios";
import Spinner from "../../components/Spinner";
import ErrorDisplay from "../../components/ErrorDisplay";
import { useAuth } from "../../auth/AuthProvider";
import { useCallback, useEffect, useState } from "react";
import StarRating from "../../components/StarRating";
import { ReviewResponseDTO } from "../../auth/AuthDTOs";
import ReviewCard from "../../components/ReviewCard";

const CoursePage = () => {
  const { courseId } = useParams<{ courseId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [course, setCourse] = useState<CourseDTO | null>(null);
  const [teachers, setTeachers] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>("");

  const [reviews, setReviews] = useState<ReviewResponseDTO[]>([]);
  const [selectedTeacher, setSelectedTeacher] = useState("");
  const [teacherScore, setTeacherScore] = useState<number | null>(null);

  const [completedCourses, setCompletedCourses] = useState<CourseDisplayDTO[]>(
    []
  );
  const [completed, setCompleted] = useState(false);
  const [togglingCompleted, setTogglingCompleted] = useState(false);

  const hasUserReviewed = reviews?.some((review) => review.owner) ?? false;

  useEffect(() => {
    if (!user) return;

    const loadCompletedCourses = async () => {
      try {
        const data = await fetchCompletedCourses();
        setCompletedCourses(data);
      } catch (err) {
        console.error("Failed to fetch completed courses", err);
      }
    };

    loadCompletedCourses();
  }, [user]);

  useEffect(() => {
    if (!course?.id) return;

    const isCompleted = completedCourses.some((c) => c.id === course.id);

    setCompleted(isCompleted);
  }, [completedCourses, course?.id]);

  const handleToggleCompleted = async () => {
    if (!user || !course?.id || togglingCompleted) return;

    try {
      setTogglingCompleted(true);

      if (completed) {
        await removeCompletedCourse(course.id.toString());
        setCompletedCourses((prev) => prev.filter((c) => c.id !== course.id));
      } else {
        await addCompletedCourse(course.id.toString());
        setCompletedCourses((prev) => [
          ...prev,
          {
            id: course.id,
            college: course.college,
            department: course.department,
            courseCode: course.courseCode,
            title: course.title,
            averageOverallRating: course.averageOverallRating,
          } as CourseDisplayDTO,
        ]);
      }
    } catch (err) {
      console.error("Failed to toggle completed course", err);
    } finally {
      setTogglingCompleted(false);
    }
  };

  useEffect(() => {
    const loadReviews = async () => {
      if (!course?.id) return;
      setLoading(true);
      try {
        const reviewData = await fetchCourseReviews(course.id, selectedTeacher);
        setReviews(reviewData);
        if (selectedTeacher !== "") {
          const teacherData = await fetchTeacherScore(
            course.id,
            selectedTeacher
          );
          setTeacherScore(teacherData);
        } else {
          setTeacherScore(null);
        }
      } catch (err: any) {
        console.log(err);
      } finally {
        setLoading(false);
      }
    };
    loadReviews();
  }, [course?.id, selectedTeacher]);

  const handleTeacherChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedTeacher(event.target.value);
  };

  const handleDeletedReview = (reviewId: number) => {
    setReviews((prevReviews) =>
      prevReviews.filter((review) => review.id !== reviewId)
    );
  };

  const loadCourse = useCallback(async () => {
    if (!courseId) {
      setError("No course Id provided");
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      setError(""); // Clear any existing errors
      const courseData = await fetchCourseById(courseId);
      const teacherNames = await fetchTeachersByCourse(courseId);
      setTeachers(teacherNames);
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
  }, [courseId]);

  useEffect(() => {
    loadCourse();
  }, [loadCourse]);

  if (loading) {
    return <Spinner />;
  }

  if (error) {
    return (
      <>
        <ErrorDisplay error={error} />
        <button
          className="btn-outline-bu-red rounded-2 p-2"
          onClick={() => navigate("/search")}
        >
          Back to Search
        </button>
      </>
    );
  }

  return (
    <div className="container py-5">
      <button
        className="btn-outline-bu-red rounded-2 p-2 border-0 mb-2"
        onClick={() => navigate("/search")}
      >
        <i className="bi bi-arrow-left me-3"></i>
        Back to Search
      </button>
      <div className="row g-4">
        <div className="col-lg-9">
          <div className="border rounded-3 p-4">
            <h1 className="fw-bold mb-4">
              {course?.college} {course?.department} {course?.courseCode}:{" "}
              {course?.title}
            </h1>
            {course?.description && course.description.length > 0 ? (
              <p className="text-muted">{course?.description}</p>
            ) : (
              <p className="text-muted">No description available</p>
            )}
            {course?.hubRequirements.length === 0 ? (
              <span className="badge badge-bu me-2">No hub reqs</span>
            ) : (
              course?.hubRequirements.map((hubReq) => (
                <span className="badge badge-bu me-2">{hubReq.name}</span>
              ))
            )}
            {course?.noPreReqs === true ? (
              <div className="text-muted mt-3">No prerequisites required</div>
            ) : (
              <div className="text-muted mt-3">Prerequisites required</div>
            )}
            <div className="border-top border-bottom d-flex flex-wrap gap-4 justify-content-between mt-3 p-3">
              <div className="text-center">
                Usefulness
                <h2 className="fw-bold">{course?.averageUsefulnessRating}/5</h2>
              </div>
              <div className="text-center">
                Difficulty
                <h2 className="fw-bold">{course?.averageDifficultyRating}/5</h2>
              </div>
              <div className="text-center">
                Workload
                <h2 className="fw-bold">{course?.averageWorkloadRating}/5</h2>
              </div>
              <div className="text-center">
                Interest
                <h2 className="fw-bold">{course?.averageInterestRating}/5</h2>
              </div>
              <div className="text-center">
                Teacher
                <h2 className="fw-bold">{course?.averageTeacherRating}/5</h2>
              </div>
            </div>
          </div>
        </div>
        <div className="col-lg-3">
          <div className="border rounded-3 p-3 text-center">
            <h3 className="fw-bold mb-3">Overall Rating</h3>
            <h1 className="fw-bold">{course?.averageOverallRating}/5</h1>
            <StarRating rating={course?.averageOverallRating ?? 0} />
            <p className="text-muted">
              Based on {course?.numReviews} review(s)
            </p>
            <div className="border-top text-start pt-3">
              <div className="fw-bold">Taken this course?</div>
              {!user ? (
                <div>
                  <p className="my-2">
                    Sign in to share your experience and help other students
                  </p>
                  <button
                    className="btn btn-bu-red w-100"
                    onClick={() => navigate("/register")}
                  >
                    Sign in to review
                  </button>
                </div>
              ) : hasUserReviewed ? (
                <div>
                  <p className="my-2 text-muted">
                    You’ve already submitted a review for this course.
                  </p>
                  <button className="btn btn-outline-bu-red w-100" disabled>
                    Review submitted
                  </button>
                </div>
              ) : (
                <div>
                  <p className="my-2">Share your experience with this course</p>
                  <button
                    className="btn btn-bu-red w-100"
                    onClick={() => navigate(`/course/${course?.id}/review`)}
                  >
                    Write a review
                  </button>
                </div>
              )}
              <button
                className="btn btn-outline-bu-red w-100 mt-3"
                onClick={handleToggleCompleted}
                disabled={togglingCompleted}
              >
                {completed ? "Completed" : "Mark completed"}
              </button>
            </div>
          </div>
        </div>
        <div className="col-lg-9">
          {/* Reviews grid */}
          <div className="border rounded-3 p-4">
            <div className="d-flex justify-content-between mb-3">
              <h3 className="fw-bold">Reviews</h3>
              <select
                className="form-select w-auto"
                value={selectedTeacher}
                onChange={handleTeacherChange}
              >
                <option value="">All Teachers</option>
                {teachers.map((teacher) => (
                  <option key={teacher} value={teacher}>
                    {teacher}
                  </option>
                ))}
              </select>
            </div>
            {reviews.length === 0 ? (
              <div className="p-5 text-center">
                <div className="text-muted">
                  No reviews yet. Be the first to write a review for this
                  course!
                </div>
                <button
                  className="btn btn-bu-red mt-3"
                  onClick={() => navigate(`/course/${course?.id}/review`)}
                >
                  Write a review
                </button>
              </div>
            ) : (
              reviews.map((review) => (
                <ReviewCard key={review.id} review={review} />
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default CoursePage;
