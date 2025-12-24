import { useState, useEffect } from "react";
import {
  fetchDepartmentsByCollege,
  fetchCoursesSearch2,
} from "../../api/axios";
import { CourseDisplayDTO } from "../../auth/AuthDTOs";
import StarRating from "../../components/StarRating";
import { Link } from "react-router-dom";

const COLLEGES = [
  "CAS",
  "CFA",
  "CDS",
  "CGS",
  "COM",
  "ENG",
  "GMS",
  "HUB",
  "KHC",
  "LAW",
  "MED",
  "MET",
  "QST",
  "SAR",
  "SDM",
  "SHA",
  "SPH",
  "SSW",
  "STH",
  "WED",
];

const HUB_REQUIREMENTS = [
  "AEX",
  "PLM",
  "HCO",
  "SI1",
  "SO1",
  "SI2",
  "SO2",
  "QR1",
  "QR2",
  "IIC",
  "GCI",
  "ETR",
  "FYW",
  "WRI",
  "WIN",
  "OSC",
  "DME",
  "CRT",
  "RIL",
  "TWC",
  "CRI",
];

const Search = () => {
  const [departments, setDepartments] = useState<string[]>([]);
  const [selectedCollege, setSelectedCollege] = useState<string>("");
  const [selectedDepartment, setSelectedDepartment] = useState("");
  const [loadingDepartments, setLoadingDepartments] = useState(false);
  const [selectedHubReqs, setSelectedHubReqs] = useState<string[]>([]);
  const [noPreReqs, setNoPreReqs] = useState(false);
  const [minRating, setMinRating] = useState<number>(0);
  const [sortBy, setSortBy] = useState<string>("byCourseCode");
  const [searchInput, setSearchInput] = useState(""); // typing
  const [searchQuery, setSearchQuery] = useState(""); // after user presses enter or search

  const [courses, setCourses] = useState<CourseDisplayDTO[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loadingCourses, setLoadingCourses] = useState(false);
  const [totalCourses, setTotalCourses] = useState(0);

  const isMobile = window.matchMedia("(max-width: 767px)").matches;
  const MAX_VISIBLE_PAGES = isMobile ? 3 : 5;
  const startPage = Math.max(0, page - Math.floor(MAX_VISIBLE_PAGES / 2));
  const endPage = Math.min(totalPages, startPage + MAX_VISIBLE_PAGES);

  const handleHubReqChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const values = Array.from(
      e.target.selectedOptions,
      (option) => option.value
    );
    setSelectedHubReqs(values);
  };

  const fetchCourses = async () => {
    setLoadingCourses(true);
    try {
      const res = await fetchCoursesSearch2(
        {
          colleges: selectedCollege ? [selectedCollege] : undefined,
          departments: selectedDepartment ? [selectedDepartment] : undefined,
          hubReqs: selectedHubReqs.length ? selectedHubReqs : undefined,
          noPreReqs,
          minRating,
          sortBy,
          searchQuery: searchQuery || undefined,
        },
        page,
        12
      );
      setCourses(res.data.content);
      setTotalPages(res.data.totalPages);
      setTotalCourses(res.data.totalElements);
    } catch (err) {
      console.error("Failed to fetch courses", err);
      setCourses([]);
    } finally {
      setLoadingCourses(false);
    }
  };

  useEffect(() => {
    fetchCourses();
  }, [
    selectedCollege,
    selectedDepartment,
    selectedHubReqs,
    noPreReqs,
    minRating,
    sortBy,
    searchQuery,
    page,
  ]);

  useEffect(() => {
    if (page !== 0) {
      setPage(0);
      return;
    }
  }, [
    selectedCollege,
    selectedDepartment,
    selectedHubReqs,
    noPreReqs,
    minRating,
    sortBy,
    searchQuery,
  ]);

  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  }, [page]);

  useEffect(() => {
    if (!selectedCollege) {
      setDepartments([]);
      setSelectedDepartment("");
      return;
    }
    setLoadingDepartments(true);
    fetchDepartmentsByCollege(selectedCollege)
      .then((res) => {
        setDepartments(res.data);
      })
      .catch(() => {
        setDepartments([]);
      })
      .finally(() => {
        setLoadingDepartments(false);
        console.log(departments);
      });
  }, [selectedCollege]);

  const resetFilters = () => {
    setSelectedCollege("");
    setSelectedDepartment("");
    setSelectedHubReqs([]);
    setNoPreReqs(false);
    setMinRating(0);
    setSortBy("byCourseCode");
    setSearchInput("");
    setSearchQuery("");
  };

  function truncateWords(text: string, maxWords: number): string {
    if (!text) return "No description available.";

    const words = text.split(/\s+/);
    if (words.length <= maxWords) return text;

    return words.slice(0, maxWords).join(" ") + "…";
  }

  return (
    <div className="container my-5">
      <h1 className="fw-bold mb-4">Search Courses</h1>
      <div className="row g-3">
        <div className="col-lg-3">
          <div className="border rounded-3 p-3">
            <div className="mb-3">
              <i className="bi bi-funnel fs-5 me-2"></i>
              <span className="h5 fw-bold">Filters</span>
            </div>
            <div className="mb-3">
              <label className="form-label fw-semibold">College</label>
              <select
                className="form-select"
                value={selectedCollege}
                onChange={(e) => setSelectedCollege(e.target.value)}
              >
                <option value="">All Colleges</option>
                {COLLEGES.map((college) => (
                  <option key={college} value={college}>
                    {college}
                  </option>
                ))}
              </select>
            </div>
            <div className="mb-3">
              <label className="form-label fw-semibold">Department</label>
              <select
                className="form-select"
                value={selectedDepartment}
                onChange={(e) => setSelectedDepartment(e.target.value)}
                disabled={!selectedCollege || loadingDepartments}
              >
                <option value="">
                  {!selectedCollege
                    ? "Select a college first"
                    : loadingDepartments
                    ? "Loading departments..."
                    : "All Departments"}
                </option>

                {departments.map((dept) => (
                  <option key={dept} value={dept}>
                    {dept}
                  </option>
                ))}
              </select>
            </div>
            <div className="mb-3">
              <label className="form-label fw-semibold">Hub Requirements</label>
              <select
                className="form-select"
                multiple
                size={5}
                value={selectedHubReqs}
                onChange={handleHubReqChange}
              >
                {HUB_REQUIREMENTS.map((hub) => (
                  <option key={hub} value={hub}>
                    {hub}
                  </option>
                ))}
              </select>
            </div>
            <div className="mb-3">
              <label className="form-label fw-semibold">
                Minimum Rating:{" "}
                <span className="text-muted">{minRating.toFixed(1)}</span>
              </label>
              <input
                type="range"
                className="form-range"
                min={0}
                max={5}
                step={0.1}
                value={minRating}
                onChange={(e) => setMinRating(parseFloat(e.target.value))}
              />
            </div>
            <div className="mb-3">
              <div className="form-check mt-2">
                <input
                  className="form-check-input"
                  type="checkbox"
                  id="noPrereqs"
                  checked={noPreReqs}
                  onChange={(e) => setNoPreReqs(e.target.checked)}
                />

                <label
                  className="form-check-label fw-semibold"
                  htmlFor="noPrereqs"
                >
                  No prerequisites required
                </label>
              </div>
            </div>
            <div className="mb-3">
              <label className="form-label fw-semibold">Sort by</label>
              <select
                className="form-select"
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
              >
                <option value="byCourseCode">Course Number</option>
                <option value="byRating">Best Rating</option>
                <option value="byReviews">Most Reviewed</option>
              </select>
            </div>
            <div>
              <button
                className="btn btn-outline-bu-red w-100"
                onClick={() => resetFilters()}
              >
                Reset Filters
              </button>
            </div>
          </div>
        </div>

        <div className="col-lg-9">
          <div className="border rounded-3 p-3">
            <form
              onSubmit={(e) => {
                e.preventDefault(); // prevent page reload
                setPage(0); // reset pagination
                setSearchQuery(searchInput.trim());
              }}
            >
              <input
                type="search" // mobile-friendly
                className="form-control"
                placeholder="Search by course code or name (e.g. CS 112 or Introduction to Computer Science)"
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
              />
            </form>
          </div>
          <p className="text-muted p-0 my-3 ms-2">{totalCourses} results</p>

          {/* Grid for displaying courses */}
          <div>
            {courses.map((course) => (
              <div key={course.id} className="border rounded-3 p-3 mb-4">
                {/* Tablet or larger viewports */}
                <div className="d-none d-md-flex justify-content-between align-items-stretch">
                  <div>
                    <h5>
                      {course.college} {course.department} {course.courseCode}:{" "}
                      {course.title}
                    </h5>
                    <p className="mt-3">
                      {truncateWords(course.description, 30)}
                    </p>
                    {course.hubRequirements.length === 0 ? (
                      <span className="badge badge-bu me-2">No hub reqs</span>
                    ) : (
                      course.hubRequirements.map((hubReq) => (
                        <span className="badge badge-bu me-2">
                          {hubReq.name}
                        </span>
                      ))
                    )}
                    <div className="mt-2">
                      <span className="text-muted small">Difficulty: </span>
                      <span className="fw-bold me-3 small">
                        {course.averageDifficultyRating}/5
                      </span>
                      {course.noPreReqs === true ? (
                        <span className="text-muted small">
                          No prerequisites required
                        </span>
                      ) : (
                        <span className="text-muted small">
                          Prerequisites required
                        </span>
                      )}
                    </div>
                  </div>
                  <div className="d-flex flex-column align-items-end text-end">
                    <div>
                      <div className="d-flex align-items-center justify-content-end gap-1 flex-nowrap">
                        <StarRating rating={course.averageOverallRating ?? 0} />
                        <span className="text-nowrap fw-bold">
                          {course.averageOverallRating?.toFixed(1) ?? "N/A"}
                        </span>
                      </div>

                      <p className="text-muted m-0 p-0 text-nowrap">
                        {course.numReviews} reviews
                      </p>
                    </div>

                    <Link
                      to={`/course/${course.id}`}
                      className="btn btn-bu-red mt-auto text-nowrap"
                    >
                      View Details
                    </Link>
                  </div>
                </div>
                {/* Mobile viewport */}
                <div className="d-flex d-md-none justify-content-between align-items-stretch">
                  <div>
                    <h5 className="mb-3">
                      {course.college} {course.department} {course.courseCode}
                    </h5>
                    {course.hubRequirements.length === 0 ? (
                      <span className="badge badge-bu me-2">No hub reqs</span>
                    ) : (
                      course.hubRequirements.map((hubReq) => (
                        <span className="badge badge-bu me-2">
                          {hubReq.name}
                        </span>
                      ))
                    )}
                    <div className="mt-3">
                      <span className="text-muted small">Difficulty: </span>
                      <span className="fw-bold me-3 small">
                        {course.averageDifficultyRating}/5
                      </span>
                    </div>
                    <div className="mt-0">
                      {course.noPreReqs === true ? (
                        <span className="text-muted small">
                          No prerequisites required
                        </span>
                      ) : (
                        <span className="text-muted small">
                          Prerequisites required
                        </span>
                      )}
                    </div>
                  </div>
                  <div className="d-flex flex-column align-items-end text-end">
                    <StarRating rating={course.averageOverallRating ?? 0} />
                    <div className="text-nowrap fw-bold me-2">
                      {course.averageOverallRating?.toFixed(1) ?? "N/A"}
                    </div>
                    <span className="text-muted m-0 p-0 text-nowrap">
                      {course.numReviews} reviews
                    </span>
                    <Link
                      to={`/course/${course.id}`}
                      className="btn btn-bu-red mt-auto text-nowrap"
                    >
                      View Details
                    </Link>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      {/* pagination */}
      {totalPages > 1 && (
        <nav className="d-flex justify-content-center mt-4">
          <ul className="pagination pagination-bu flex-wrap">
            {/* Previous */}
            <li className={`page-item ${page === 0 ? "disabled" : ""}`}>
              <button
                className="page-link"
                onClick={() => setPage((p) => Math.max(p - 1, 0))}
              >
                ‹ Prev
              </button>
            </li>

            {/* First page + ellipsis (desktop only) */}
            {!isMobile && startPage > 0 && (
              <>
                <li className="page-item">
                  <button className="page-link" onClick={() => setPage(0)}>
                    1
                  </button>
                </li>
                <li className="page-item disabled">
                  <span className="page-link ellipsis">…</span>
                </li>
              </>
            )}

            {/* Page window */}
            {Array.from(
              { length: endPage - startPage },
              (_, i) => startPage + i
            ).map((i) => (
              <li key={i} className={`page-item ${page === i ? "active" : ""}`}>
                <button className="page-link" onClick={() => setPage(i)}>
                  {i + 1}
                </button>
              </li>
            ))}

            {/* Last page + ellipsis (desktop only) */}
            {!isMobile && endPage < totalPages && (
              <>
                <li className="page-item disabled">
                  <span className="page-link ellipsis">…</span>
                </li>
                <li className="page-item">
                  <button
                    className="page-link"
                    onClick={() => setPage(totalPages - 1)}
                  >
                    {totalPages}
                  </button>
                </li>
              </>
            )}

            {/* Next */}
            <li
              className={`page-item ${
                page === totalPages - 1 ? "disabled" : ""
              }`}
            >
              <button
                className="page-link"
                onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
              >
                Next ›
              </button>
            </li>
          </ul>
        </nav>
      )}
    </div>
  );
};

export default Search;
