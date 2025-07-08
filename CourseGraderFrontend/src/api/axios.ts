import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api", 
  headers: {
    "Content-Type": "application/json",
  },
});

// automatically attach token to all requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export const fetchColleges = () => api.get("/courses/colleges");

export const fetchDepartmentsByCollege = (college: string) =>
  api.get(`/courses/departments/${college}`);

export const fetchFilteredCourses = (filters: {
  colleges?: string[];
  departments?: string[];
  hubs?: string[];
  minRating?: number;
  maxDifficulty?: number;
  maxWorkload?: number;
  minUsefulness?: number;
  minInterest?: number;
  minTeacher?: number;
  noPreReqs?: boolean;
  minCourseCode?: number;
  reviewCount?: number;
}) => {
  const params = new URLSearchParams();

  if (filters.colleges) {
    filters.colleges.forEach((college) => params.append("colleges", college));
  }

  if (filters.departments) {
    filters.departments.forEach((dept) => params.append("departments", dept));
  }

  if (filters.hubs) {
    filters.hubs.forEach((hub) => params.append("hubs", hub));
  }

  if (filters.minRating !== undefined)
    params.append("minRating", filters.minRating.toString());

  if (filters.maxDifficulty !== undefined)
    params.append("maxDifficulty", filters.maxDifficulty.toString());

  if (filters.maxWorkload !== undefined)
    params.append("maxWorkload", filters.maxWorkload.toString());

  if (filters.minUsefulness !== undefined)
    params.append("minUsefulness", filters.minUsefulness.toString());

  if (filters.minInterest !== undefined)
    params.append("minInterest", filters.minInterest.toString());

  if (filters.minTeacher !== undefined)
    params.append("minTeacher", filters.minTeacher.toString());

  if (filters.minCourseCode !== undefined)
    params.append("minCourseCode", filters.minCourseCode.toString());

  if (filters.reviewCount !== undefined)
    params.append("reviewCount", filters.reviewCount.toString());

  if (filters.noPreReqs !== undefined)
    params.append("noPreReqs", filters.noPreReqs.toString());

  return api.get(`/courses/search?${params.toString()}`);
}

export default api;
