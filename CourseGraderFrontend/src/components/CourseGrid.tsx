import GridCard from "./GridCard";
import { CourseDisplayDTO } from "../auth/AuthDTOs";

interface Props {
  courses: CourseDisplayDTO[];
}

const CourseGrid = ({ courses }: Props) => {
  if (courses.length === 0) {
    return (
      <p className="text-muted text-center mt-5 mb-5">
        No courses found. Try adjusting the filter parameters.
      </p>
    );
  }
  return (
    <div className="container mt-4 mb-5">
      <div className="row g-4">
        {courses.map((course) => (
          <div key={course.id} className="col-12 col-md-6 col-lg-4">
            <GridCard course={course} />
          </div>
        ))}
      </div>
    </div>
  );
};

export default CourseGrid;
