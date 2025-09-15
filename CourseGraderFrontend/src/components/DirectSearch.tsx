import React, { useState } from "react";
import { fetchCourseByQuery } from "../api/axios";

interface DirectSearchProps {
  onSearchResults: (
    results: any[],
    paginationInfo: any,
    searchMode: "direct",
    query: string
  ) => void;
  onLoading: (loading: boolean) => void;
  onClear: () => void;
}

const DirectSearch = ({
  onSearchResults,
  onLoading,
  onClear,
}: DirectSearchProps) => {
  const [courseSearchQuery, setCourseSearchQuery] = useState("");

  const handleDirectCourseSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!courseSearchQuery.trim()) return;

    try {
      onLoading(true);

      const response = await fetchCourseByQuery(courseSearchQuery, 0, 18);

      const paginationInfo = {
        totalElements: response.data.totalElements,
        totalPages: response.data.totalPages,
        currentPage: response.data.number,
        pageSize: response.data.size,
      };

      onSearchResults(
        response.data.content,
        paginationInfo,
        "direct",
        courseSearchQuery
      );
    } catch (err) {
      console.error("Error fetching course by query:", err);
    } finally {
      onLoading(false);
    }
  };

  return (
    <div className="container mb-3">
      <h4 className="h3 text-center text-danger my-3">
        Search for Specific Courses
      </h4>
      <form onSubmit={handleDirectCourseSearch}>
        <div className="d-flex gap-2">
          <input
            type="text"
            className="form-control flex-grow-1"
            placeholder="Type in course code or title"
            value={courseSearchQuery}
            onChange={(e) => setCourseSearchQuery(e.target.value)}
          />
          <button
            type="submit"
            className="btn btn-danger"
            disabled={!courseSearchQuery.trim()}
          >
            Search
          </button>
        </div>
      </form>
    </div>
  );
};

export default DirectSearch;
