import React, { useState } from "react";
import Filter from "../../components/Filter";
import { fetchFilteredCourses } from "../../api/axios";
import CourseGrid from "../../components/CourseGrid";

const Search = () => {
  const [filters, setFilters] = useState({});
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [sortBy, setSortBy] = useState("byCourseCode");

  const handleSearch = async (searchFilters = filters) => {
    try {
      setLoading(true);
      const response = await fetchFilteredCourses(searchFilters);
      setResults(response.data.content);
    } catch (err) {
      console.error("Error fetching filtered courses", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Filter
        setFilters={setFilters}
        onSearch={handleSearch}
        setSortBy={setSortBy}
        sortBy={sortBy}
      />
      <CourseGrid courses={results} />
    </div>
  );
};

export default Search;
