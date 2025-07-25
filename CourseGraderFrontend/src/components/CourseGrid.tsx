import GridCard from "./GridCard";
import { CourseDisplayDTO } from "../auth/AuthDTOs";

interface Props {
  courses: CourseDisplayDTO[];
  onRefresh?: () => void; // Optional refresh callback
}

const CourseGrid = ({ courses, onRefresh }: Props) => {
  if (courses.length === 0) {
    return (
      <div
        className="d-flex align-items-center justify-content-center"
        style={{ minHeight: "30vh" }}
      >
        <p className="text-muted text-center">
          No courses found. Try adjusting the filter parameters.
        </p>
      </div>
    );
  }
  return (
    <div className="container mt-4 mb-5">
      <div className="row g-4">
        {courses.map((course) => (
          <div key={course.id} className="col-12 col-md-6 col-lg-4">
            <GridCard course={course} onRefresh={onRefresh} />
          </div>
        ))}
      </div>
    </div>
  );
};

export default CourseGrid;
