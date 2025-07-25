import axios from "axios";
import { CourseDisplayDTO, UserDashboardDTO } from "../auth/AuthDTOs";

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

export const fetchDashboardData = async (): Promise<UserDashboardDTO> => {
  const response = await api.get<UserDashboardDTO>("/users/dashboard")
  return response.data
}

export const addCompletedCourse = (courseId: string) => api.post(`/users/completed-courses/${courseId}`);

export const addSavedCourse = (courseId: string) => api.post(`/users/saved-courses/${courseId}`);

export const addInProgressCourse = (courseId: string) => api.post(`/users/courses-in-progress/${courseId}`);

export const removeCompletedCourse = (courseId: string) => api.delete(`/users/completed-courses/${courseId}`);

export const removeSavedCourse = (courseId: string) => api.delete(`/users/saved-courses/${courseId}`);

export const removeInProgressCourse = (courseId: string) => api.delete(`/users/courses-in-progress/${courseId}`);

export const fetchCompletedCourses = async (): Promise<CourseDisplayDTO[]> => {
  const response = await api.get<CourseDisplayDTO[]>("/users/completed-courses");
  return response.data;
};

export const fetchSavedCourses = async (): Promise<CourseDisplayDTO[]> => {
  const response = await api.get<CourseDisplayDTO[]>("/users/saved-courses");
  return response.data;
};

export const fetchCoursesInProgress = async (): Promise<CourseDisplayDTO[]> => {
  const response = await api.get<CourseDisplayDTO[]>("/users/courses-in-progress");
  return response.data;
};

export const fetchColleges = () => api.get("/courses/colleges");

export const fetchFullColleges = () => api.get("/auth/colleges");

export const fetchMajorsByFullCollege = (college: string) => api.get(`/auth/majors/${college}`);

export const fetchDepartmentsByCollege = (college: string) =>
  api.get(`/courses/departments/${college}`);

export const fetchFilteredCourses = (filters: {
  colleges?: string[];
  departments?: string[];
  hubReqs?: string[];
  minRating?: number;
  maxDifficulty?: number;
  maxWorkload?: number;
  minUsefulness?: number;
  minInterest?: number;
  minTeacher?: number;
  noPreReqs?: boolean;
  minCourseCode?: number;
  reviewCount?: number;
  sortBy?: string;
}, page: number = 0) => {
  const params = new URLSearchParams();

  if (filters.colleges) {
    filters.colleges.forEach((college) => params.append("colleges", college));
  }

  if (filters.departments) {
    filters.departments.forEach((dept) => params.append("departments", dept));
  }

  if (filters.hubReqs) {
    filters.hubReqs.forEach((hub) => params.append("hubReqs", hub));
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

  if (filters.sortBy)
    params.append("sortBy", filters.sortBy);

  params.append("page", page.toString());

  return api.get(`/courses/search?${params.toString()}`);
}

export default api;
