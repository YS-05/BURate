import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import api from "../../api/axios";
import { useNavigate, Link } from "react-router-dom";

const schema = z.object({
  email: z.string().email("Invalid email address"),
  password: z
    .string()
    .min(6, "Password must be at least 6 characters")
    .max(100, "Password is too long"),
});

type RegisterForm = z.infer<typeof schema>;

const Register = () => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<RegisterForm>({
    resolver: zodResolver(schema),
  });

  const navigate = useNavigate();

  const onSubmit = async (formData: RegisterForm) => {
    try {
      const res = await api.post("/auth/register", formData);
      alert(res.data.message);
      localStorage.setItem("verificationEmail", formData.email); // save email
      reset();
      navigate("/verify"); // redirect to verification page
    } catch (error: any) {
      const message =
        error.response?.data?.message || "Registration failed. Try again.";
      alert(message);
    }
  };

  return (
    <div
      className="d-flex justify-content-center align-items-center"
      style={{ minHeight: "calc(100vh - 225px)", backgroundColor: "#f5f5f5" }}
    >
      <div className="container" style={{ maxWidth: "480px", width: "100%" }}>
        <h2 className="text-center mb-4">Sign up to start rating BU courses</h2>
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
            <label className="form-label">Password:</label>
            <input
              type="password"
              className={`form-control ${errors.password ? "is-invalid" : ""}`}
              placeholder="••••••••"
              {...register("password")}
            />
            {errors.password && (
              <div className="invalid-feedback">{errors.password.message}</div>
            )}
          </div>
          <button
            type="submit"
            className="btn btn-danger w-100 mb-4"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Registering..." : "Register"}
          </button>
        </form>
        <div className="text-center">
          <span className="text-muted">Already have an account? </span>
          <Link to="/login" className="text-decoration-none">
            Log in here
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Register;
