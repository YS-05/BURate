import { ReviewResponseDTO } from "../auth/AuthDTOs";
import ReviewItem from "./ReviewItem";
import { useEffect, useState } from "react";
import { fetchCourseReviews, fetchTeacherScore } from "../api/axios";
import Spinner from "./Spinner";

interface Props {
  id: string | undefined;
  teachers: string[];
}

const ReviewCard = ({ id, teachers }: Props) => {
  const [loading, setLoading] = useState(false);
  const [reviews, setReviews] = useState<ReviewResponseDTO[]>([]);
  const [selectedTeacher, setSelectedTeacher] = useState("");
  const [teacherScore, setTeacherScore] = useState<number | null>(null);

  const hasUserReviewed = reviews?.some((review) => review.owner) ?? false;

  useEffect(() => {
    const loadReviews = async () => {
      if (!id) return;
      setLoading(true);
      try {
        const reviewData = await fetchCourseReviews(id, selectedTeacher);
        setReviews(reviewData);
        if (selectedTeacher !== "") {
          const teacherData = await fetchTeacherScore(id, selectedTeacher);
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
  }, [id, selectedTeacher]);

  const handleTeacherChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedTeacher(event.target.value);
  };

  const handleDeletedReview = (reviewId: number) => {
    setReviews((prevReviews) =>
      prevReviews.filter((review) => review.id !== reviewId)
    );
  };

  if (loading) {
    return <Spinner />;
  }

  return (
    <div className="card border-danger rounded-0 mb-4">
      <div className="card-header">
        <div className="d-flex justify-content-between align-items-center">
          <h5 className="mb-0">Reviews: {reviews?.length || 0}</h5>
          <div className="dropdown-container">
            <select
              className="form-select"
              value={selectedTeacher}
              onChange={handleTeacherChange}
              style={{ minWidth: "200px" }}
            >
              <option value="">All Teachers</option>
              {teachers.map((teacher, index) => (
                <option key={index} value={teacher}>
                  {teacher}
                </option>
              ))}
            </select>
          </div>
        </div>
        {teacherScore && (
          <div>
            Professor {selectedTeacher} has an average rating of{" "}
            <span className="fw-bold">{teacherScore.toFixed(1)}</span> when
            teaching this course
          </div>
        )}
      </div>

      <div className="card-body">
        {reviews && reviews.length > 0 ? (
          <div className="d-grid gap-3">
            {reviews.map((review) => (
              <ReviewItem
                key={review.id}
                review={review}
                onReviewDeleted={handleDeletedReview}
              />
            ))}
          </div>
        ) : (
          <div className="text-muted">No reviews yet.</div>
        )}
      </div>
    </div>
  );
};

export default ReviewCard;
