type StarRatingProps = {
  rating: number; // 0–5
};

const StarRating = ({ rating }: StarRatingProps) => {
  const fullStars = Math.floor(rating);
  const partialFill = (rating - fullStars) * 100;

  return (
    <div className="d-inline-flex align-items-center">
      {[...Array(5)].map((_, i) => {
        if (i < fullStars) {
          return <i key={i} className="bi bi-star-fill text-warning me-1" />;
        }

        if (i === fullStars && partialFill > 0) {
          return (
            <div
              key={i}
              className="position-relative d-inline-block me-1"
              style={{ width: "1em" }}
            >
              <i className="bi bi-star text-warning" />
              <div
                className="position-absolute top-0 start-0 overflow-hidden"
                style={{ width: `${partialFill}%` }}
              >
                <i className="bi bi-star-fill text-warning" />
              </div>
            </div>
          );
        }

        return <i key={i} className="bi bi-star text-warning me-1" />;
      })}
    </div>
  );
};

export default StarRating;
