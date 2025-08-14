export type RegisterUserDTO = {
  email: string;
  college: string;
  major: string;
  expectedGrad: number;
  password: string;
}

export type LoginUserDTO = {
  email: string;
  password: string;
};

export type VerifyUserDTO = {
  email: string;
  verificationCode: string;
};

export type PasswordResetDTO = {
  email: string;
  verificationCode: string;
  newPassword: string;
};

export type ResendVerificationOrPasswordDTO = {
  email: string;
};

export type HubRequirement =
  | "PLM"
  | "AEX"
  | "HCO"
  | "SI1"
  | "SI2"
  | "SO1"
  | "SO2"
  | "QR1"
  | "QR2"
  | "IIC"
  | "GCI"
  | "ETR"
  | "FYW"
  | "WRI"
  | "WIN"
  | "OSC"
  | "DME"
  | "CRT"
  | "RIL"
  | "TWC"
  | "CRI";

export type HubRequirementDTO = {
  name: string;
};

export type CourseDisplayDTO = {
  id: string;
  title: string;
  college: string;
  department: string;
  courseCode: string;
  noPreReqs: boolean;
  numReviews: number;
  averageOverallRating: number;
  averageUsefulnessRating: number;
  averageDifficultyRating: number;
  averageWorkloadRating: number;
  averageInterestRating: number;
  averageTeacherRating: number;
  hubRequirements: HubRequirementDTO[];
}

export type UserDashboardDTO = {
  email: string;
  expectedGrad: number;
  college: string;
  major: string;
  coursesCompleted: number;
  coursesInProgress: number;
  coursesSaved: number;
  coursesReviewed: number;
  totalUpvotes: number;
  averageReviewScore: number;
  coursesToReview: Set<string>;
};

export type HubProgressDTO = {
  hubProgress: HubProgressItem[];
}

export type HubProgressItem = {
  hubCode: string;
  hubName: string;
  category: string;
  required: number;
  completed: number;
  projected: number;
  projectedFulfilled: boolean;
  fulfilled: boolean;
  fulfillingCourses: string[];
}

export type CourseDTO = {
  id: string;
  title: string;
  college: string;
  department: string;
  courseCode: string;
  description: string;
  noPreReqs: boolean;
  numReviews: number;
  averageOverallRating: number;
  averageUsefulnessRating: number;
  averageDifficultyRating: number;
  averageWorkloadRating: number;
  averageInterestRating: number;
  averageTeacherRating: number;
  hubRequirements: HubRequirementDTO[];
  userReviewed: boolean;
}

export enum VoteType {
  UPVOTE = "UPVOTE",
  DOWNVOTE = "DOWNVOTE"
}

export type ReviewResponseDTO = {
  id: number;
  courseId: number;
  usefulnessRating: number;
  difficultyRating: number;
  workloadRating: number;
  interestRating: number;
  overallRating: number;
  teacherRating: number;
  teacherName: string;
  reviewText: string;
  semester: string;
  hoursPerWeek: number;
  assignmentTypes: string; 
  attendanceRequired: boolean;
  createdAt: string; 
  owner: boolean;
  upvoteCount: number;
  downvoteCount: number;
  userVote: VoteType | null;
}

export type CreateReviewDTO = {
 usefulnessRating: number; // @Min(1) @Max(5)
 difficultyRating: number; // @Min(1) @Max(5)
 workloadRating: number; // @Min(1) @Max(5)
 interestRating: number; // @Min(1) @Max(5)
 teacherRating: number; // @Min(1) @Max(5)
 teacherName: string; // @Size(max = 100)
 reviewText: string; // @Size(max = 2000)
 semester: string; // @Size(max = 100)
 hoursPerWeek: number; // @Min(0) @Max(40)
 assignmentTypes: string; // @Size(max = 100) 
 attendanceRequired: boolean;
}

export type VoteResponseDTO = {
  reviewId: string;
  voteCount: number;
  userVote: string | null;
}

export type AccountDTO = {
  college: string;
  major: string;
  expectedGrad: number;
}