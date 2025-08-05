import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAuth } from "../../auth/AuthProvider";
import { createReview, fetchCourseById } from "../../api/axios";
import { CourseDTO } from "../../auth/AuthDTOs";
import Spinner from "../../components/Spinner";
import ErrorDisplay from "../../components/ErrorDisplay";
import CourseHeader from "../../components/CourseHeader";
import CourseRatings from "../../components/CourseRatings";

const schema = z.object({
  usefulnessRating: z.number().min(1).max(5),
  difficultyRating: z.number().min(1).max(5),
  workloadRating: z.number().min(1).max(5),
  interestRating: z.number().min(1).max(5),
  teacherRating: z.number().min(1).max(5),
  teacherName: z.string().min(1).max(100),
  reviewText: z.string().min(1).max(2000),
  semester: z.string().min(1).max(100),
  hoursPerWeek: z.number().min(0).max(40),
  assignmentTypes: z.string().min(1).max(100),
  attendanceRequired: z.boolean(),
});

type ReviewForm = z.infer<typeof schema>;

const Review = () => {
  const { courseId } = useParams<{ courseId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [course, setCourse] = useState<CourseDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<ReviewForm>({
    resolver: zodResolver(schema),
    defaultValues: {
      usefulnessRating: 3,
      difficultyRating: 3,
      workloadRating: 3,
      interestRating: 3,
      teacherRating: 3,
    },
  });

  const ratings = watch([
    "usefulnessRating",
    "difficultyRating",
    "workloadRating",
    "interestRating",
    "teacherRating",
  ]);

  useEffect(() => {
    if (!user) {
      navigate("/login");
      return;
    }

    const loadCourses = async () => {
      if (!courseId) {
        setError("No course ID provided");
        setLoading(false);
        return;
      }
      try {
        const courseData = await fetchCourseById(courseId);
        setCourse(courseData);
        const userReview = courseData.courseReviews?.find(
          (review) => review.owner
        );
        if (userReview) {
          setError("You can only review this course once");
        }
      } catch (err) {
        console.log(err);
        setError("Failed to load course details");
      } finally {
        setLoading(false);
      }
    };
    loadCourses();
  }, [courseId, user, navigate]);

  const onSubmit = async (formData: ReviewForm) => {
    if (!courseId) {
      setError("No course ID provided");
      return;
    }
    try {
      await createReview(courseId, formData);
      navigate(`/course/${courseId}`, {
        state: { message: "Review submitted successfully!" },
      });
    } catch (err: any) {
      console.log("Error submitting review:", err);
      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError("Failed to submit review. Please try again.");
      }
    }
  };

  const handleRatingChange = (index: number, value: number) => {
    const ratingFields = [
      "usefulnessRating",
      "difficultyRating",
      "workloadRating",
      "interestRating",
      "teacherRating",
    ] as const;

    setValue(ratingFields[index], value);
  };

  if (loading) {
    return <Spinner />;
  }

  if (error) {
    return <ErrorDisplay error={error} />;
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
            onClick={() => navigate(`/course/${courseId}`)}
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
        <div className="card border-danger rounded-0">
          <div className="card-body">
            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <CourseRatings
                ratings={ratings}
                onRatingChange={handleRatingChange}
              />
              <div className="mb-5">
                <h5 className="mb-5" style={{ color: "#e57373" }}>
                  Course Details
                </h5>
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label fw-semibold">
                      Professor Name <span className="text-danger">*</span>
                    </label>
                    <input
                      type="text"
                      className={`form-control ${
                        errors.teacherName ? "is-invalid" : ""
                      }`}
                      id="teacherName"
                      {...register("teacherName")}
                    />
                    {errors.teacherName && (
                      <div className="invalid-feedback">
                        {errors.teacherName.message}
                      </div>
                    )}
                  </div>
                  <div className="col-md-6 mb-3">
                    <label
                      htmlFor="semester"
                      className="form-label fw-semibold"
                    >
                      Semester Taken <span className="text-danger">*</span>
                    </label>
                    <input
                      type="text"
                      className={`form-control ${
                        errors.semester ? "is-invalid" : ""
                      }`}
                      id="semester"
                      placeholder="e.g. Fall 2024"
                      {...register("semester")}
                    />
                    {errors.semester && (
                      <div className="invalid-feedback">
                        {errors.semester.message}
                      </div>
                    )}
                  </div>
                  <div className="col-md-6 mb-3">
                    <label
                      htmlFor="hoursPerWeek"
                      className="form-label fw-semibold"
                    >
                      Hours per Week <span className="text-danger">*</span>
                    </label>
                    <input
                      type="number"
                      className={`form-control ${
                        errors.hoursPerWeek ? "is-invalid" : ""
                      }`}
                      id="hoursPerWeek"
                      placeholder="0-40"
                      {...register("hoursPerWeek", { valueAsNumber: true })}
                    />
                    <div className="form-text">
                      Time spent on this course per week
                    </div>
                    {errors.hoursPerWeek && (
                      <div className="invalid-feedback">
                        {errors.hoursPerWeek.message}
                      </div>
                    )}
                  </div>
                  <div className="col-md-6 mb-3">
                    <label className="form-label fw-semibold">
                      Attendance Required?{" "}
                      <span className="text-danger">*</span>
                    </label>
                    <div className="mt-2">
                      <div className="form-check form-check-inline">
                        <input
                          className="form-check-input"
                          type="radio"
                          id="attendanceYes"
                          value="true"
                          {...register("attendanceRequired", {
                            setValueAs: (value) => value === "true",
                          })}
                        />
                        <label
                          className="form-check-label"
                          htmlFor="attendanceYes"
                        >
                          Yes
                        </label>
                      </div>
                      <div className="form-check form-check-inline">
                        <input
                          className="form-check-input"
                          type="radio"
                          id="attendanceNo"
                          value="false"
                          {...register("attendanceRequired", {
                            setValueAs: (value) => value === "false",
                          })}
                        />
                        <label
                          className="form-check-label"
                          htmlFor="attendanceNo"
                        >
                          No
                        </label>
                      </div>
                    </div>
                    {errors.attendanceRequired && (
                      <div className="text-danger small mt-1">
                        {errors.attendanceRequired.message}
                      </div>
                    )}
                  </div>
                </div>
                <div className="mb-3">
                  <label
                    htmlFor="assignmentTypes"
                    className="form-label fw-semibold"
                  >
                    Assignment Types <span className="text-danger">*</span>
                  </label>
                  <input
                    type="text"
                    className={`form-control ${
                      errors.assignmentTypes ? "is-invalid" : ""
                    }`}
                    id="assignmentTypes"
                    placeholder="e.g., Essays, Problem Sets, Group Projects, Exams"
                    {...register("assignmentTypes")}
                  />
                  {errors.assignmentTypes && (
                    <div className="invalid-feedback">
                      {errors.assignmentTypes.message}
                    </div>
                  )}
                </div>
                <div className="mb-5">
                  <h5 className="mb-4" style={{ color: "#e57373" }}>
                    Written Review
                  </h5>

                  <div className="mb-3">
                    <label
                      htmlFor="reviewText"
                      className="form-label fw-semibold"
                    >
                      Your Review <span className="text-danger">*</span>
                    </label>
                    <textarea
                      className={`form-control ${
                        errors.reviewText ? "is-invalid" : ""
                      }`}
                      id="reviewText"
                      rows={6}
                      placeholder="Share your thoughts about the course, professor, assignments, what you learned, tips for future students, etc."
                      {...register("reviewText")}
                    />
                    <div className="form-text">Character limit: 2000</div>
                    {errors.reviewText && (
                      <div className="invalid-feedback">
                        {errors.reviewText.message}
                      </div>
                    )}
                  </div>
                </div>
                <button
                  type="submit"
                  className="btn btn-danger btn-lg"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? (
                    <>
                      <span
                        className="spinner-border spinner-border-sm me-2"
                        role="status"
                        aria-hidden="true"
                      ></span>
                      Submitting...
                    </>
                  ) : (
                    "Submit Review"
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Review;
