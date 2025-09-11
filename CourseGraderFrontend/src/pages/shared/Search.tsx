import React, { useEffect, useState } from "react";
import Filter from "../../components/Filter";
import DirectSearch from "../../components/DirectSearch";
import { fetchFilteredCourses, fetchCourseByQuery } from "../../api/axios";
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
  const [results, setResults] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [sortBy, setSortBy] = useState("byCourseCode");
  const [currentPage, setCurrentPage] = useState(0);
  const [paginationInfo, setPaginationInfo] = useState<PaginationInfo>({
    totalElements: 0,
    totalPages: 0,
    currentPage: 0,
    pageSize: 18,
  });

  // State for tracking search mode and query
  const [searchMode, setSearchMode] = useState<"filter" | "direct">("filter");
  const [currentDirectQuery, setCurrentDirectQuery] = useState("");

  useEffect(() => {
    handleFilterSearch(filters, currentPage);
  }, []);

  const handleFilterSearch = async (searchFilters = filters, page = 0) => {
    try {
      setLoading(true);
      setSearchMode("filter");
      setCurrentDirectQuery("");

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
      console.error("Error fetching filtered courses");
    } finally {
      setLoading(false);
    }
  };

  const handleDirectSearchResults = (
    searchResults: any[],
    searchPaginationInfo: any,
    mode: "direct",
    query: string
  ) => {
    setResults(searchResults);
    setPaginationInfo(searchPaginationInfo);
    setSearchMode(mode);
    setCurrentDirectQuery(query);
    setCurrentPage(0);
  };

  const handleDirectSearchPaginated = async (page: number) => {
    if (!currentDirectQuery.trim()) return;

    try {
      setLoading(true);
      const response = await fetchCourseByQuery(currentDirectQuery, page, 18);

      setResults(response.data.content);
      setPaginationInfo({
        totalElements: response.data.totalElements,
        totalPages: response.data.totalPages,
        currentPage: response.data.number,
        pageSize: response.data.size,
      });
      setCurrentPage(page);
    } catch (err) {
      console.error("Error fetching course by query:", err);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (page: number) => {
    if (searchMode === "filter") {
      handleFilterSearch(filters, page);
    } else if (searchMode === "direct") {
      handleDirectSearchPaginated(page);
    }
  };

  const handleDirectSearchClear = () => {
    setSearchMode("filter");
    setCurrentDirectQuery("");
    handleFilterSearch(filters, 0);
  };

  return (
    <div
      style={{
        backgroundColor: "#f5f5f5",
        padding: "30px",
      }}
    >
      {/* Direct Course Search Section */}
      <DirectSearch
        onSearchResults={handleDirectSearchResults}
        onLoading={setLoading}
        onClear={handleDirectSearchClear}
      />

      <div className="container mb-2">
        <div className="d-flex justify-content-center">
          <span className="h4 text-muted px-3">OR</span>
        </div>
      </div>

      {/* Filter Search Section */}
      <Filter
        setFilters={setFilters}
        onSearch={(searchFilters) => handleFilterSearch(searchFilters, 0)}
        setSortBy={setSortBy}
        sortBy={sortBy}
      />

      {/* Results Section */}
      {paginationInfo.totalElements > 0 && (
        <div className="container">
          <div className="mb-3">
            <span className="text-muted">
              {searchMode === "direct"
                ? `Found ${paginationInfo.totalElements} course(s) matching "${currentDirectQuery}"`
                : `Found ${paginationInfo.totalElements} courses`}
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

      {/* Show pagination when there are multiple pages */}
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
