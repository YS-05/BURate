import React, { useEffect, useState } from "react";
import { useParams, useNavigate, useSearchParams } from "react-router-dom";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAuth } from "../../auth/AuthProvider";
import {
  createReview,
  fetchCourseById,
  fetchReviewById,
  updateReview,
} from "../../api/axios";
import { CourseDTO, ReviewResponseDTO } from "../../auth/AuthDTOs";
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
  assignmentTypes: z.string().min(1).max(500),
  attendanceRequired: z.boolean(),
});

type ReviewForm = z.infer<typeof schema>;

const Review = () => {
  const { courseId } = useParams<{ courseId: string }>();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const editReviewId = searchParams.get("edit");

  const [course, setCourse] = useState<CourseDTO | null>(null);
  const [existingReview, setExistingReview] =
    useState<ReviewResponseDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const hasExistingReview = existingReview !== null;

  const {
    register,
    handleSubmit,
    watch,
    reset,
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
      attendanceRequired: false,
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

    const loadData = async () => {
      if (!courseId) {
        setError("No course ID provided");
        setLoading(false);
        return;
      }
      try {
        const courseData = await fetchCourseById(courseId);
        setCourse(courseData);
        if (editReviewId) {
          const reviewData = await fetchReviewById(editReviewId);
          setExistingReview(reviewData);
          reset({
            usefulnessRating: reviewData.usefulnessRating,
            difficultyRating: reviewData.difficultyRating,
            workloadRating: reviewData.workloadRating,
            interestRating: reviewData.interestRating,
            teacherRating: reviewData.teacherRating,
            teacherName: reviewData.teacherName,
            reviewText: reviewData.reviewText,
            semester: reviewData.semester,
            hoursPerWeek: reviewData.hoursPerWeek,
            assignmentTypes: reviewData.assignmentTypes,
            attendanceRequired: reviewData.attendanceRequired,
          });
        }
      } catch (err) {
        console.log(err);
        if (editReviewId) {
          setError("Failed to load review details");
        } else {
          setError("Failed to load course details");
        }
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [courseId, editReviewId, user, navigate, reset]);

  const onSubmit = async (formData: ReviewForm) => {
    if (!courseId) {
      setError("No course ID provided");
      return;
    }
    try {
      if (hasExistingReview && editReviewId) {
        await updateReview(editReviewId, formData);
        navigate(`/course/${courseId}`, {
          state: { message: "Review updated successfully!" },
        });
      } else {
        await createReview(courseId, formData);
        navigate(`/course/${courseId}`, {
          state: { message: "Review submitted successfully!" },
        });
      }
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
    <div className="container my-5">
      <div className="mb-5">
        <button
          className="btn btn-outline-bu-red"
          onClick={() => navigate(`/course/${courseId}`)}
        >
          ← Back to course
        </button>
      </div>
      <h1 className="fw-bold mb-4">
        {" "}
        {course?.college} {course?.courseCode} {course?.department}:{" "}
        {course?.title}
      </h1>
      <div className="card border border-dark rounded-3">
        <div className="card-body p-2 p-lg-4">
          <form onSubmit={handleSubmit(onSubmit)} noValidate>
            <CourseRatings
              ratings={ratings}
              onRatingChange={handleRatingChange}
            />
            <div>
              <h5 className="my-5 fw-bold">Course Details</h5>
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
                  <label htmlFor="semester" className="form-label fw-semibold">
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
                    Attendance Required? <span className="text-danger">*</span>
                  </label>
                  <div className="mt-2">
                    <div className="form-check mb-3">
                      <input
                        type="checkbox"
                        className={`form-check-input ${
                          errors.attendanceRequired ? "is-invalid" : ""
                        }`}
                        id="attendanceRequired"
                        {...register("attendanceRequired")}
                      />
                      <label
                        className="form-check-label"
                        htmlFor="attendanceRequired"
                      >
                        Attendance Required
                      </label>
                      {errors.attendanceRequired && (
                        <div className="invalid-feedback">
                          {errors.attendanceRequired.message}
                        </div>
                      )}
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
              <div>
                <h5 className="mt-5">
                  Written Review <span className="text-danger">*</span>
                </h5>
                <div>
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
                className="btn btn-bu-red mt-3"
                disabled={isSubmitting}
              >
                {isSubmitting ? (
                  <>
                    <span
                      className="spinner-border spinner-border-sm me-2"
                      role="status"
                      aria-hidden="true"
                    ></span>
                    {hasExistingReview ? "Updating..." : "Submitting..."}
                  </>
                ) : hasExistingReview ? (
                  "Update Review"
                ) : (
                  "Submit Review"
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Review;
