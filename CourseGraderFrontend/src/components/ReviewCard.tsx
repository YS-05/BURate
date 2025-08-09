import { ReviewResponseDTO } from "../auth/AuthDTOs";
import { useAuth } from "../auth/AuthProvider";
import { useNavigate } from "react-router-dom";
import ReviewItem from "./ReviewItem";
import { useEffect, useState } from "react";
import { fetchCourseReviews } from "../api/axios";
import Spinner from "./Spinner";

interface Props {
  id: string | undefined;
  teachers: string[];
}

const ReviewCard = ({ id, teachers }: Props) => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const [reviews, setReviews] = useState<ReviewResponseDTO[]>([]);
  const [selectedTeacher, setSelectedTeacher] = useState("");
  const hasUserReviewed = reviews?.some((review) => review.owner) ?? false;

  useEffect(() => {
    const loadReviews = async () => {
      if (!id) return;
      setLoading(true);
      try {
        const reviewData = await fetchCourseReviews(id, selectedTeacher);
        setReviews(reviewData);
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

  if (loading) {
    return <Spinner />;
  }

  return (
    <div className="card border-danger rounded-0 mb-4">
      <div className="card-header d-flex justify-content-between align-items-center">
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

      <div className="card-body">
        {reviews && reviews.length > 0 ? (
          <div className="d-grid gap-3">
            {reviews
              .sort(
                (a, b) =>
                  new Date(b.createdAt).getTime() -
                  new Date(a.createdAt).getTime()
              )
              .map((review) => (
                <ReviewItem key={review.id} review={review} />
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
