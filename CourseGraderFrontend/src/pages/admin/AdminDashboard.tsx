import { syncCoursesToRAG, syncReviewsToRAG } from "../../api/axios";

const AdminDashboard = () => {
  const handleSyncCourses = async () => {
    try {
      const res = await syncCoursesToRAG();
      alert(typeof res === "string" ? res : res.message);
    } catch (err) {
      console.error(err);
      alert("Course sync failed");
    }
  };

  const handleSyncReviews = async () => {
    try {
      const res = await syncReviewsToRAG();
      alert(typeof res === "string" ? res : res.message);
    } catch (err) {
      console.error(err);
      alert("Review sync failed");
    }
  };

  return (
    <div className="container my-5">
      <h1 className="fw-bold">Admin Commands</h1>
      <button className="btn btn-bu-red me-3" onClick={handleSyncCourses}>
        Sync courses
      </button>
      <button className="btn btn-bu-red" onClick={handleSyncReviews}>
        Sync reviews
      </button>
    </div>
  );
};

export default AdminDashboard;
