import axios from "axios";
import { CourseDisplayDTO, UserDashboardDTO, HubProgressDTO, CourseDTO, CreateReviewDTO, ReviewResponseDTO, VoteResponseDTO, AccountDTO, UpdatePasswordDTO, ContactUsDTO, PasswordResetDTO } from "../auth/AuthDTOs";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
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

export const addSavedCourse = (courseId: string) => api.post(`/users/saved-courses/${courseId}`);

export const addInProgressCourse = (courseId: string) => api.post(`/users/courses-in-progress/${courseId}`);

export const removeCompletedCourse = (courseId: string) => api.delete(`/users/completed-courses/${courseId}`);

export const removeSavedCourse = (courseId: string) => api.delete(`/users/saved-courses/${courseId}`);

export const removeInProgressCourse = (courseId: string) => api.delete(`/users/courses-in-progress/${courseId}`);

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

export const fetchCourseByQuery = async (query: string, page = 0, size = 18) => {
  return await api.get('/courses/query', {
    params: {
      query: query.trim(),
      page,
      size
    }
  });
}

export const fetchMyReviews = async (): Promise<ReviewResponseDTO[]> => {
  const response = await api.get<ReviewResponseDTO[]>("/reviews/my-reviews");
  return response.data;
};

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
};

export default api;