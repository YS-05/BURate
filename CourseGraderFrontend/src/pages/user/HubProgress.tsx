import React, { useEffect, useState } from "react";
import { fetchHubProgress } from "../../api/axios";
import { HubProgressDTO, HubProgressItem } from "../../auth/AuthDTOs";
import { useAuth } from "../../auth/AuthProvider";
import Spinner from "../../components/Spinner";
import ErrorDisplay from "../../components/ErrorDisplay";

const HubProgress = () => {
  const [loading, setLoading] = useState(true);
  const [hubProgress, setHubProgress] = useState<HubProgressItem[]>([]);
  const { user } = useAuth();
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (user) {
      loadHubProgress();
    }
  }, [user]);

  const loadHubProgress = async () => {
    try {
      setLoading(true);
      const data: HubProgressDTO = await fetchHubProgress();
      setHubProgress(data.hubProgress);
    } catch (err) {
      console.log("Error fetching hub progress", err);
      setError("Error fetching hub progress");
    } finally {
      setLoading(false);
    }
  };

  const getProgressPercent = (completed: number, required: number) => {
    return required > 0 ? Math.min((completed / required) * 100, 100) : 0;
  };

  const getProgressColor = (fulfilled: boolean, percentage: number) => {
    if (fulfilled) return "success-subtle";
    if (percentage >= 50) return "info";
    return "danger";
  };

  const getStatus = (fulfilled: boolean, projectedFulfilled: boolean) => {
    if (fulfilled) {
      return (
        <span
          className="badge"
          style={{ backgroundColor: "#20c997", color: "white" }}
        >
          ✓ Complete
        </span>
      );
    }
    if (projectedFulfilled) {
      return <span className="badge bg-info">Will Complete</span>;
    }
    return <span className="badge bg-danger">Needs Attention</span>;
  };

  if (!user) {
    return (
      <div className="container mt-5">
        <div className="alert alert-info" role="alert">
          Error getting your hub progress, please re-log in
        </div>
      </div>
    );
  }

  if (error) {
    return <ErrorDisplay error={error} />;
  }

  if (loading) {
    return <Spinner />;
  }

  const groupedProgress: Record<string, HubProgressItem[]> = {};
  for (const item of hubProgress) {
    if (!groupedProgress[item.category]) {
      groupedProgress[item.category] = [];
    }
    groupedProgress[item.category].push(item);
  }

  const totalRequirements = hubProgress.length;
  const fulfilledRequirements = hubProgress.filter(
    (item) => item.fulfilled
  ).length;
  const projectedFulfilled = hubProgress.filter(
    (item) => item.projectedFulfilled
  ).length;
  const overallProgress = getProgressPercent(
    fulfilledRequirements,
    totalRequirements
  );

  return (
    <div
      className="min-vh-100"
      style={{ backgroundColor: "#f5f5f5", padding: "30px" }}
    >
      <div className="container">
        <div className="text-center text-danger mb-5">
          <h3 className="mb-4">Hub Requirements Progress</h3>
          <p className="text-muted">
            Track your progress towards completing Boston University's Hub
            Requirements
          </p>
        </div>

        <div className="row mb-5">
          <div className="col-12">
            <div className="card border-danger rounded-0">
              <div className="card-body">
                <div className="row align-items-center">
                  <div className="col-md-8 px-3 py-2">
                    <h5 className="card-title mb-3">Overall Progress</h5>
                    <div className="progress mb-3" style={{ height: "25px" }}>
                      <div
                        className="progress-bar bg-danger"
                        role="progressbar"
                        style={{ width: `${overallProgress}%` }}
                        aria-valuenow={overallProgress}
                        aria-valuemin={0}
                        aria-valuemax={100}
                      >
                        {Math.round(overallProgress)}%
                      </div>
                    </div>
                    <div className="row text-center">
                      <div className="col-4">
                        <div
                          className="fw-bold fs-4"
                          style={{ color: "#20c997" }}
                        >
                          {fulfilledRequirements}
                        </div>
                        <small className="text-muted">Completed</small>
                      </div>
                      <div className="col-4">
                        <div className="fw-bold text-info fs-4">
                          {projectedFulfilled - fulfilledRequirements}
                        </div>
                        <small className="text-muted">In Future</small>
                      </div>
                      <div className="col-4">
                        <div className="fw-bold text-danger fs-4">
                          {totalRequirements - projectedFulfilled}
                        </div>
                        <small className="text-muted">Remaining</small>
                      </div>
                    </div>
                  </div>
                  <div className="col-md-4 text-center">
                    <div className="display-4 fw-bold text-danger">
                      {Math.round(overallProgress)}%
                    </div>
                    <div className="text-muted">Complete</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {Object.entries(groupedProgress).map(([category, items]) => {
          const categoryFulfilled = items.filter(
            (item) => item.fulfilled
          ).length;
          const categoryTotal = items.length;
          const categoryProgress = getProgressPercent(
            categoryFulfilled,
            categoryTotal
          );

          return (
            <div key={category} className="mb-5">
              <div className="card border-danger rounded-0">
                <div className="card-header">
                  <div className="row align-items-center">
                    <div className="col-md-8">
                      <h5 className="mb-0">{category}</h5>
                    </div>
                    <div className="col-md-4 text-end">
                      <span className="badge bg-light text-dark fs-6">
                        {categoryFulfilled}/{categoryTotal} Complete
                      </span>
                    </div>
                  </div>
                  <div className="progress mt-2" style={{ height: "8px" }}>
                    <div
                      className="progress-bar bg-danger"
                      role="progressbar"
                      style={{ width: `${categoryProgress}%` }}
                    ></div>
                  </div>
                </div>
                <div className="card-body">
                  <div className="row g-4">
                    {items.map((item) => {
                      const percentage = getProgressPercent(
                        item.completed,
                        item.required
                      );
                      const progressColor = getProgressColor(
                        item.fulfilled,
                        percentage
                      );
                      return (
                        <div key={item.hubCode} className="col-lg-6 col-xl-4">
                          <div className="card h-100 rounded-0">
                            <div className="card-body">
                              <div className="d-flex justify-content-between align-items-start mb-3">
                                <div>
                                  <h6 className="card-title fw-bold text-danger mb-1">
                                    {item.hubCode}
                                  </h6>
                                  <p className="card-text small text-muted mb-2">
                                    {item.hubName}
                                  </p>
                                </div>
                                {getStatus(
                                  item.fulfilled,
                                  item.projectedFulfilled
                                )}
                              </div>

                              <div className="mb-3">
                                <div className="d-flex justify-content-between small mb-1">
                                  <span className="fw-semibold">Progress</span>
                                  <span className="fw-bold">
                                    {Math.round(percentage)}%
                                  </span>
                                </div>
                                <div
                                  className="progress mb-2"
                                  style={{ height: "12px" }}
                                >
                                  <div
                                    className={`progress-bar bg-${progressColor}`}
                                    role="progressbar"
                                    style={{ width: `${percentage}%` }}
                                  ></div>
                                </div>
                                <div className="small text-muted">
                                  {item.completed} of {item.required} required
                                  {item.projected > item.completed && (
                                    <span className="text-info ms-2">
                                      +{item.projected - item.completed}{" "}
                                      projected
                                    </span>
                                  )}
                                </div>
                              </div>
                              {item.fulfillingCourses &&
                                item.fulfillingCourses.length > 0 && (
                                  <div className="mb-3">
                                    <div className="small fw-semibold text-muted mb-2">
                                      Fulfilling Courses:
                                    </div>
                                    <div className="d-flex flex-wrap gap-1">
                                      {item.fulfillingCourses.map(
                                        (course: string, index: number) => (
                                          <span
                                            key={index}
                                            className="badge bg-danger-subtle text-danger border"
                                          >
                                            {course}
                                          </span>
                                        )
                                      )}
                                    </div>
                                  </div>
                                )}
                              <div className="border-top pt-3">
                                <div className="row small">
                                  <div className="col-6">
                                    <div className="text-muted">Status:</div>
                                    <div
                                      className="fw-semibold"
                                      style={{
                                        color: item.fulfilled
                                          ? "#20c997"
                                          : "#0dcaf0",
                                      }}
                                    >
                                      {item.fulfilled
                                        ? "Complete"
                                        : "In Progress"}
                                    </div>
                                  </div>
                                  <div className="col-6">
                                    <div className="text-muted">
                                      Projection:
                                    </div>
                                    <div
                                      className="fw-semibold"
                                      style={{
                                        color: item.projectedFulfilled
                                          ? "#0dcaf0"
                                          : "#dc3545",
                                      }}
                                    >
                                      {item.fulfilled
                                        ? "Already Complete" // Already complete
                                        : item.projectedFulfilled
                                        ? "Will Complete" // Will be complete
                                        : "Needs Attention"}
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default HubProgress;
