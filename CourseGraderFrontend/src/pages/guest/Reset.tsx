import React from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
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
    } catch (err) {
      console.log(err);
      setError("No account found with this email");
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
        <h2 className="text-center mb-4">
          Enter your email to recieve the password reset code
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
  );
};

export default Reset;
