import React, { useEffect, useState } from "react";
import Filter from "../../components/Filter";
import { fetchFilteredCourses } from "../../api/axios";
import CourseGrid from "../../components/CourseGrid";
import Pagination from "../../components/Pagination";

interface PaginationInfo {
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

const Search = () => {
  const [filters, setFilters] = useState({});
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [sortBy, setSortBy] = useState("byCourseCode");
  const [currentPage, setCurrentPage] = useState(0);
  const [paginationInfo, setPaginationInfo] = useState<PaginationInfo>({
    totalElements: 0,
    totalPages: 0,
    currentPage: 0,
    pageSize: 18,
  });

  useEffect(() => {
    handleSearch(filters, currentPage);
  }, []);

  const handleSearch = async (searchFilters = filters, page = 0) => {
    try {
      setLoading(true);
      const response = await fetchFilteredCourses(searchFilters, page);
      setResults(response.data.content);
      setPaginationInfo({
        totalElements: response.data.totalElements,
        totalPages: response.data.totalPages,
        currentPage: response.data.number,
        pageSize: response.data.size,
      });
      setCurrentPage(page);
    } catch (err) {
      console.error("Error fetching filtered courses", err);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (page: number) => {
    handleSearch(filters, page);
  };

  return (
    <div
      style={{
        backgroundColor: "#f5f5f5",
        padding: "30px",
      }}
    >
      <Filter
        setFilters={setFilters}
        onSearch={(searchFilters) => handleSearch(searchFilters, 0)}
        setSortBy={setSortBy}
        sortBy={sortBy}
      />

      {paginationInfo.totalElements > 0 && (
        <div className="container">
          <div className="mb-3">
            <span className="text-muted">
              Found {paginationInfo.totalElements} courses
            </span>
          </div>
        </div>
      )}

      {loading && (
        <div className="container text-center my-5">
          <div className="spinner-border text-danger" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      )}

      {!loading && <CourseGrid courses={results} />}

      {paginationInfo.totalPages > 1 && (
        <Pagination
          currentPage={currentPage}
          totalPages={paginationInfo.totalPages}
          onPageChange={handlePageChange}
        />
      )}
    </div>
  );
};

export default Search;
