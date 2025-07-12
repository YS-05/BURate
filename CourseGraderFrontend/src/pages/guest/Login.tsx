import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import api from "../../api/axios";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../../auth/AuthProvider";
import { useState } from "react";

const schema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().min(6).max(100),
});

type LoginForm = z.infer<typeof schema>;

const Login = () => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<LoginForm>({
    resolver: zodResolver(schema),
  });

  const navigate = useNavigate();
  const { login } = useAuth();
  const [loginError, setLoginError] = useState<string>("");

  const onSubmit = async (formdata: LoginForm) => {
    setLoginError("");
    try {
      const res = await api.post("/auth/login", formdata);
      const token = res.data.token;
      await login(token);
      reset();
      navigate("/dashboard");
    } catch (error: any) {
      const message =
        error.response?.data?.message || "Login failed. Please try again.";
      setLoginError(message);
    }
  };

  return (
    <div
      className="d-flex justify-content-center align-items-center"
      style={{ minHeight: "calc(100vh - 225px)", backgroundColor: "#f5f5f5" }}
    >
      <div className="container" style={{ maxWidth: "480px", width: "100%" }}>
        <h2 className="text-center mb-4">Log in to BU Rate</h2>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="mb-3">
            <label className="form-label">Email address:</label>
            <input
              type="email"
              className={`form-control ${errors.email ? "is-invalid" : ""}`}
              placeholder="you@example.com"
              {...register("email")}
            />
          </div>
          <div className="mb-4">
            <label className="form-label">Password:</label>
            <input
              type="password"
              className={`form-control ${errors.password ? "is-invalid" : ""}`}
              placeholder="••••••••"
              {...register("password")}
            />
            {loginError && (
              <div
                className="alert alert-danger mb-4 mt-3 py-2 px-3"
                role="alert"
              >
                {loginError}
              </div>
            )}
          </div>
          <button
            type="submit"
            className="btn btn-danger w-100 mb-4"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Logging In" : "Log In"}
          </button>
        </form>
        <div className="text-center">
          <span className="text-muted">Don't have an account? </span>
          <Link to="/register" className="text-decoration-none">
            Sign up here
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;
