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