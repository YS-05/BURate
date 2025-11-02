import { Helmet } from "react-helmet-async";
import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import { forgotPassword } from "../../api/axios";

const schema = z.object({
  email: z.string().email("Invalid email address"),
});

type ResetForm = z.infer<typeof schema>;

const Reset = () => {
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResetForm>({
    resolver: zodResolver(schema),
  });

  const onSubmit = async (data: ResetForm) => {
    setLoading(true);
    setError("");
    try {
      await forgotPassword(data.email);
      localStorage.setItem("resetEmail", data.email);
      navigate("/reset-password");
    } catch (err: any) {
      console.log(err);
      const message = err.response?.data?.message;
      setError(message || "Reset failed, please try again later");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {/* SEO Metadata */}
      <Helmet>
        <title>Reset Password | BU Rate</title>
        <meta
          name="description"
          content="Reset your BU Rate account password securely. Enter your registered email to receive a verification code and regain access."
        />
        <meta
          name="keywords"
          content="BU Rate reset password, BU Rate forgot password, BU Rate recover account, Boston University course review login reset"
        />
        <link rel="canonical" href="https://burate.com/reset" />

        <meta property="og:type" content="website" />
        <meta property="og:title" content="Reset Password | BU Rate" />
        <meta
          property="og:description"
          content="Forgot your BU Rate password? Enter your email to receive a reset code and regain access to your account."
        />
        <meta property="og:url" content="https://burate.com/reset" />
        <meta
          property="og:image"
          content="https://burate.com/images/og-banner.png"
        />

        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content="Reset Password | BU Rate" />
        <meta
          name="twitter:description"
          content="Reset your BU Rate password quickly and securely."
        />
        <meta
          name="twitter:image"
          content="https://burate.com/images/og-banner.png"
        />
      </Helmet>

      {/* Page Content */}
      <div
        className="d-flex justify-content-center align-items-center p-5"
        style={{ minHeight: "calc(100vh - 225px)", backgroundColor: "#f5f5f5" }}
      >
        <div className="container" style={{ maxWidth: "480px", width: "100%" }}>
          <h2 className="text-center mb-4">
            Enter your email to receive the password reset code
          </h2>
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="my-4">
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
              {loading ? "Sending..." : "Send Reset Code"}
            </button>
          </form>
        </div>
      </div>
    </>
  );
};

export default Reset;
