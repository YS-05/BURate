import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import { resetPassword } from "../../api/axios";

const schema = z
  .object({
    resetCode: z.string().length(6, "Reset code must be exactly 6 characters"),
    newPassword: z.string().min(6, "Password must be at least 6 characters"),
    confirmPassword: z.string().min(6, "Please confirm your password"),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });
type SetPasswordForm = z.infer<typeof schema>;

const SetPassword = () => {
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [email, setEmail] = useState("");
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<SetPasswordForm>({
    resolver: zodResolver(schema),
  });

  useEffect(() => {
    const resetEmail = localStorage.getItem("resetEmail");
    if (!resetEmail) {
      navigate("/reset");
      return;
    }
    setEmail(resetEmail);
  }, [navigate]);

  const onSubmit = async (data: SetPasswordForm) => {
    setLoading(true);
    setError("");

    try {
      await resetPassword({
        email,
        verificationCode: data.resetCode,
        newPassword: data.newPassword,
      });
      localStorage.removeItem("resetEmail");
      navigate("/login");
    } catch (err: any) {
      console.log(err);
      const message =
        err.response?.data?.message || "Reset failed, try again later";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="d-flex justify-content-center align-items-center p-5"
      style={{ minHeight: "calc(100vh - 225px)", backgroundColor: "#f5f5f5" }}
    >
      <div className="container" style={{ maxWidth: "480px", width: "100%" }}>
        <h2 className="text-center mb-4">Set New Password</h2>
        <p className="text-center text-muted mb-4">
          Enter the reset code sent to {email}
        </p>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="mb-3">
            <input
              type="text"
              className={`form-control ${errors.resetCode ? "is-invalid" : ""}`}
              placeholder="Reset code"
              {...register("resetCode")}
            />
            {errors.resetCode && (
              <div className="invalid-feedback">{errors.resetCode.message}</div>
            )}
          </div>

          <div className="mb-3">
            <input
              type="password"
              className={`form-control ${
                errors.newPassword ? "is-invalid" : ""
              }`}
              placeholder="New password"
              {...register("newPassword")}
            />
            {errors.newPassword && (
              <div className="invalid-feedback">
                {errors.newPassword.message}
              </div>
            )}
          </div>

          <div className="mb-3">
            <input
              type="password"
              className={`form-control ${
                errors.confirmPassword ? "is-invalid" : ""
              }`}
              placeholder="Confirm new password"
              {...register("confirmPassword")}
            />
            {errors.confirmPassword && (
              <div className="invalid-feedback">
                {errors.confirmPassword.message}
              </div>
            )}
          </div>

          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          <button
            type="submit"
            className="btn btn-primary w-100"
            disabled={loading}
          >
            {loading ? "Resetting..." : "Reset Password"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default SetPassword;
