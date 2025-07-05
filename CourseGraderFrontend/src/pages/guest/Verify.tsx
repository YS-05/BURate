import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import api from "../../api/axios";

const schema = z.object({
  email: z.string().email(),
  verificationCode: z
    .string()
    .min(6, "Enter the 6-digit code")
    .max(6, "Enter the 6-digit code"),
});

type VerifyForm = z.infer<typeof schema>;

const Verify = () => {
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

  const onSubmit = async (formData: VerifyForm) => {
    try {
      const res = await api.post("/auth/verify", formData);
      alert(res.data.message);
      localStorage.removeItem("verificationEmail"); // 🧹 clean up
      window.location.href = "/login";
    } catch (error: any) {
      alert("Verification failed");
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
        </form>
      </div>
    </div>
  );
};

export default Verify;
