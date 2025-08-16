import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { updatePassword } from "../api/axios";
import ErrorDisplay from "./ErrorDisplay";

const passwordSchema = z
  .object({
    currentPassword: z.string().min(1, "Current password is required"),
    newPassword: z.string().min(6, "Password must be at least 6 characters"),
    confirmPassword: z.string().min(6, "Please confirm your new password"),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });

type PasswordForm = z.infer<typeof passwordSchema>;

const ResetPassword = () => {
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<PasswordForm>({
    resolver: zodResolver(passwordSchema),
  });

  const onSubmit = async (formData: PasswordForm) => {
    try {
      setError("");
      setSuccess("");
      await updatePassword({
        oldPassword: formData.currentPassword,
        newPassword: formData.newPassword,
      });
      reset();
      setSuccess("Password updated successfully!");
    } catch (err: any) {
      console.log(err);
      setError(err.response?.data?.message || "Incorrect current password");
    }
  };

  return (
    <div className="card">
      <div className="card-body border-danger border">
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="mb-3">
            <label className="form-label fw-semibold">Current Password:</label>
            <input
              type="password"
              className={`form-control ${
                errors.currentPassword ? "is-invalid" : ""
              }`}
              {...register("currentPassword")}
            />
            {errors.currentPassword && (
              <div className="invalid-feedback">
                {errors.currentPassword.message}
              </div>
            )}
          </div>
          <div className="mb-3">
            <label className="form-label fw-semibold">New Password:</label>
            <input
              type="password"
              className={`form-control ${
                errors.newPassword ? "is-invalid" : ""
              }`}
              {...register("newPassword")}
            />
            {errors.newPassword && (
              <div className="invalid-feedback">
                {errors.newPassword.message}
              </div>
            )}
          </div>
          <div className="mb-3">
            <label className="form-label fw-semibold">
              Confirm New Password:
            </label>
            <input
              type="password"
              className={`form-control ${
                errors.confirmPassword ? "is-invalid" : ""
              }`}
              {...register("confirmPassword")}
            />
            {errors.confirmPassword && (
              <div className="invalid-feedback">
                {errors.confirmPassword.message}
              </div>
            )}
          </div>
          <button
            type="submit"
            className="btn btn-outline-danger w-100"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Updating..." : "Update Password"}
          </button>
          {error && <p className="text-danger mt-2">{error}</p>}
          {success && <p className="text-success mt-2">{success}</p>}
        </form>
      </div>
    </div>
  );
};

export default ResetPassword;
