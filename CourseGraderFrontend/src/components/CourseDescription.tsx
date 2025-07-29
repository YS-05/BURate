interface Props {
  description: string | undefined;
}

const CourseDescription = ({ description }: Props) => {
  return (
    <div className="card border-danger rounded-0 mb-4">
      <div className="card-header">
        <h5 className="mb-0">Course Description</h5>
      </div>
      <div className="card-body">
        {!description && <p className="mb-0">No Description Found</p>}
        <p className="mb-0">{description}</p>
      </div>
    </div>
  );
};

export default CourseDescription;
