import { Helmet } from "react-helmet-async";
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
      console.log(error);
      const message =
        error.response?.data?.message || "Incorrect email or password";
      setLoginError(message);
    }
  };

  return (
    <>
      {/* SEO Metadata */}
      <Helmet>
        <title>Login | BU Rate</title>
        <meta
          name="description"
          content="Log in to BU Rate to access your personalized dashboard, course reviews, and Hub tracking for Boston University."
        />
        <meta
          name="keywords"
          content="BU Rate login, Boston University account, BU course reviews login, BU Hub tracker login"
        />
        <link rel="canonical" href="https://burate.com/login" />

        <meta property="og:type" content="website" />
        <meta property="og:title" content="Login | BU Rate" />
        <meta
          property="og:description"
          content="Access your BU Rate account to review courses, manage Hub progress, and explore student insights."
        />
        <meta property="og:url" content="https://burate.com/login" />
        <meta
          property="og:image"
          content="https://burate.com/images/og-banner.png"
        />

        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content="Login | BU Rate" />
        <meta
          name="twitter:description"
          content="Log in to BU Rate — your Boston University course review and Hub management platform."
        />
        <meta
          name="twitter:image"
          content="https://burate.com/images/og-banner.png"
        />
      </Helmet>

      {/* Page Content */}
      <div
        className="d-flex justify-content-center align-items-center"
        style={{ minHeight: "calc(100vh - 225px)" }}
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
                className={`form-control ${
                  errors.password ? "is-invalid" : ""
                }`}
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
              className="btn btn-bu-red w-100 mb-4"
              disabled={isSubmitting}
            >
              {isSubmitting ? "Logging In" : "Log In"}
            </button>
          </form>
          <div className="text-center">
            <div className="text-muted mb-2">
              Forgot password?{" "}
              <Link className="text-decoration-none" to="/reset">
                Reset here
              </Link>
            </div>
            <span className="text-muted">Don't have an account? </span>
            <Link to="/register" className="text-decoration-none">
              Sign up here
            </Link>
          </div>
        </div>
      </div>
    </>
  );
};

export default Login;
