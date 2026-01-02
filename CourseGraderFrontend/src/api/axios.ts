import axios from "axios";
import { CourseDisplayDTO, UserDashboardDTO, HubProgressDTO, CourseDTO, CreateReviewDTO, ReviewResponseDTO, VoteResponseDTO, AccountDTO, UpdatePasswordDTO, ContactUsDTO, PasswordResetDTO, ChatRequestDTO } from "../auth/AuthDTOs";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL, // baseURL: "http://localhost:8080/api" for local
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
  const response = await api.get<UserDashboardDTO>("/users/dashboard");
  return response.data;
};

export const addCompletedCourse = (courseId: string) => api.post(`/users/completed-courses/${courseId}`);

export const verifyAccount = (verificationData: { email: string; verificationCode: string }) => 
  api.post("/auth/verify", verificationData);

export const resendVerification = (email: string) => 
  api.post(`/auth/resend-verification?email=${email}`);


export const removeCompletedCourse = (courseId: string) => api.delete(`/users/completed-courses/${courseId}`);

export const fetchCourseById = async (courseId: string): Promise<CourseDTO> => {
  const response = await api.get<CourseDTO>(`/courses/${courseId}`);
  return response.data;
};

export const createReview = async (courseId: string, reviewData: CreateReviewDTO): Promise<ReviewResponseDTO> => {
  const response = await api.post<ReviewResponseDTO>(`/reviews/course/${courseId}`, reviewData);
  return response.data;
};

export const deleteReview = async (reviewId: string): Promise<{ message: string }> => {
  const response = await api.delete<{ message: string }>(`/reviews/${reviewId}`);
  return response.data;
};

export const updateReview = async (reviewId: string, reviewData: CreateReviewDTO): Promise<ReviewResponseDTO> => {
  const response = await api.put<ReviewResponseDTO>(`/reviews/${reviewId}`, reviewData);
  return response.data;
};

export const fetchReviewById = async (reviewId: string): Promise<ReviewResponseDTO> => {
  const response = await api.get<ReviewResponseDTO>(`/reviews/${reviewId}`);
  return response.data;
};

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

export const fetchReviewVotes = async (reviewId: string): Promise<VoteResponseDTO> => {
  const response = await api.get<VoteResponseDTO>(`/votes/review/${reviewId}`);
  return response.data;
};

export const voteOnReview = async (reviewId: string, voteType: string): Promise<VoteResponseDTO> => {
  const response = await api.post<VoteResponseDTO>(`/votes/review/${reviewId}?voteType=${voteType}`);
  return response.data;
};

export const fetchTeachersByCourse = async (courseId: string): Promise<string[]> => {
  const response = await api.get(`/reviews/course/${courseId}/teachers`);
  return response.data;
};

export const fetchCourseReviews = async (courseId: string, teacherName?: string): Promise<ReviewResponseDTO[]> => {
  const params = new URLSearchParams();
  if (teacherName) {
    params.append("teacherName", teacherName);
  }
  const url = `/reviews/course/${courseId}${params.toString() ? `?${params.toString()}` : ''}`;
  const response = await api.get<ReviewResponseDTO[]>(url);
  return response.data;
};

export const fetchMyReviews = async (): Promise<ReviewResponseDTO[]> => {
  const response = await api.get<ReviewResponseDTO[]>("/reviews/my-reviews");
  return response.data;
};

export const fetchAllReviews = async (): Promise<ReviewResponseDTO[]> => {
  const response = await api.get<ReviewResponseDTO[]>("/reviews/all-reviews");
  return response.data;
}

export const fetchTeacherScore = async (courseId: string, teacherName: string): Promise<number> => {
  const response = await api.get<number>(`/reviews/course/${courseId}/teacherScore?teacherName=${encodeURIComponent(teacherName)}`);
  return response.data;
};

export const fetchFullColleges = () => api.get("/auth/colleges");

export const fetchMajorsByFullCollege = (college: string) => 
  api.get(`/auth/majors?college=${encodeURIComponent(college)}`);

export const fetchDepartmentsByCollege = (college: string) =>
  api.get(`/courses/departments/${college}`);

export const getAccount = async (): Promise<AccountDTO> => {
  const response = await api.get<AccountDTO>("/users/account");
  return response.data;
};

export const updateAccount = (accountInfo: AccountDTO) => 
  api.put('/users/account', accountInfo);

export const fetchHubProgress = async (): Promise<HubProgressDTO> => {
  const response = await api.get<HubProgressDTO>("/users/hub-progress");
  return response.data;
};

export const sendContactMessage = async (contactData: ContactUsDTO): Promise<{ message: string }> => {
  const response = await api.post<{ message: string }>("/auth/contact", contactData);
  return response.data;
};

export const updatePassword = (passwordData: UpdatePasswordDTO) => 
  api.put('/auth/update-password', passwordData);

export const resetPassword = (data: PasswordResetDTO) => 
  api.post("/auth/reset-password", data);

export const deleteUser = () => api.delete("/users/delete");

export const forgotPassword = (email: string) => 
  api.post(`/auth/forgot-password?email=${email}`);


export const fetchCoursesSearch = async (
  filters: {
    colleges?: string[];
    departments?: string[];
    hubReqs?: string[];
    noPreReqs?: boolean;
    minRating?: number;
    sortBy?: string;
    searchQuery?: string;
  },
  page: number = 0,
  size: number = 12
) => {
  const params = new URLSearchParams();

  filters.colleges?.forEach(c => params.append("colleges", c));
  filters.departments?.forEach(d => params.append("departments", d));
  filters.hubReqs?.forEach(h => params.append("hubReqs", h));

  if (filters.noPreReqs !== undefined)
    params.append("noPreReqs", String(filters.noPreReqs));

  if (filters.minRating !== undefined)
    params.append("minRating", String(filters.minRating));

  if (filters.sortBy)
    params.append("sortBy", filters.sortBy);

  if (filters.searchQuery)
    params.append("searchQuery", filters.searchQuery);

  params.append("page", String(page));
  params.append("size", String(size));

  return api.get(`/courses/search?${params.toString()}`);
};

export const syncCoursesToRAG = async (): Promise<{ message: string } | string> => {
  const response = await api.post("/ai/sync-courses");
  return response.data;
};

export const syncReviewsToRAG = async (): Promise<{ message: string } | string> => {
  const response = await api.post("/ai/sync-reviews");
  return response.data;
};

export const askAIAdvisor = async (request: ChatRequestDTO): Promise<string> => {
  const response = await api.post<string>("/ai/chat", request);
  return response.data;
};

export default api;