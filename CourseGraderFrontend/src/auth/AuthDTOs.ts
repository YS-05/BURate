export type RegisterLoginUserDTO = {
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
