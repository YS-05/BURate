import React, { useState } from "react";
import Filter from "../../components/Filter";
import { fetchFilteredCourses } from "../../api/axios";
import CourseGrid from "../../components/CourseGrid";

const Search = () => {
  const [filters, setFilters] = useState({});
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    try {
      const response = await fetchFilteredCourses(filters);
      setResults(response.data.content); // this will be passed to CourseGrid
    } catch (err) {
      console.error("Error fetching filtered courses", err);
    }
  };

  return (
    <div>
      <Filter setFilters={setFilters} onSearch={handleSearch} />
      <CourseGrid courses={results} />
    </div>
  );
};

export default Search;
