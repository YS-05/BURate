import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { verifyAccount, resendVerification } from "../../api/axios";

const schema = z.object({
  email: z.string().email(),
  verificationCode: z
    .string()
    .min(6, "Enter the 6-digit code")
    .max(6, "Enter the 6-digit code"),
});

type VerifyForm = z.infer<typeof schema>;

const Verify = () => {
  const [error, setError] = useState("");
  const [resendError, setResendError] = useState("");
  const [isResending, setIsResending] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);

  const {
    register,
    handleSubmit,
    setValue, //  used to prefill email
    formState: { errors, isSubmitting },
  } = useForm<VerifyForm>({
    resolver: zodResolver(schema),
  });

  // Pre-fill email from localStorage
  useEffect(() => {
    const savedEmail = localStorage.getItem("verificationEmail");
    if (savedEmail) {
      setValue("email", savedEmail);
    }
  }, [setValue]);

  // For cooldown
  useEffect(() => {
    let timer: number;
    if (resendCooldown > 0) {
      timer = setTimeout(() => {
        setResendCooldown(resendCooldown - 1);
      }, 1000);
    }
    return () => clearTimeout(timer);
  }, [resendCooldown]);

  const onSubmit = async (formData: VerifyForm) => {
    try {
      await verifyAccount(formData);
      localStorage.removeItem("verificationEmail"); // 🧹 clean up
      window.location.href = "/login";
    } catch (err: any) {
      const message =
        err.response?.data?.message || "Failed to verify, try again later";
      setError(message);
    }
  };

  const handleResendEmail = async () => {
    const savedEmail = localStorage.getItem("verificationEmail");
    if (!savedEmail) {
      setResendError("No email found");
      return;
    }
    if (resendCooldown > 0) return;
    try {
      setIsResending(true);
      setResendError("");
      await resendVerification(savedEmail);
      setResendCooldown(60);
    } catch (err: any) {
      console.log(err);
      const message =
        err.response?.data?.message ||
        "Failed to resend email, try again later";
      setResendError(message);
    } finally {
      setIsResending(false);
    }
  };

  return (
    <div
      className="d-flex justify-content-center align-items-center"
      style={{ minHeight: "calc(100vh - 225px)", backgroundColor: "#f5f5f5" }}
    >
      <div className="container" style={{ maxWidth: "480px" }}>
        <h2 className="text-center mb-4">
          Please check your email for the verification code
        </h2>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="mb-3">
            <label className="form-label">Email address:</label>
            <input
              type="email"
              disabled
              className={`form-control ${errors.email ? "is-invalid" : ""}`}
              placeholder="you@example.com"
              {...register("email")}
            />
            {errors.email && (
              <div className="invalid-feedback">{errors.email.message}</div>
            )}
          </div>
          <div className="mb-4">
            <label className="form-label">Verification code:</label>
            <input
              type="text"
              className={`form-control ${
                errors.verificationCode ? "is-invalid" : ""
              }`}
              placeholder="6-digit code"
              {...register("verificationCode")}
            />
            {errors.verificationCode && (
              <div className="invalid-feedback">
                {errors.verificationCode.message}
              </div>
            )}
          </div>
          <button
            type="submit"
            className="btn btn-success w-100"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Verifying..." : "Verify Account"}
          </button>
          {error && <div className="text-danger text-center mb-4">{error}</div>}

          <div className="text-center my-4">
            <button
              type="button"
              className="btn btn-link text-decoration-none p-0"
              onClick={handleResendEmail}
              disabled={isResending || resendCooldown > 0}
            >
              {isResending
                ? "Sending..."
                : resendCooldown > 0
                ? `Resend verification email (${resendCooldown}s)`
                : "Resend verification email"}
            </button>
          </div>

          {resendError && (
            <div className="text-danger text-center mb-2">{resendError}</div>
          )}
        </form>
      </div>
    </div>
  );
};

export default Verify;
