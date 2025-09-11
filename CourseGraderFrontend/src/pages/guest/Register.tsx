import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import api from "../../api/axios";
import { useNavigate, Link } from "react-router-dom";
import { fetchFullColleges, fetchMajorsByFullCollege } from "../../api/axios";
import { useEffect, useState } from "react";

const schema = z.object({
  email: z.string().email("Invalid email address"),
  college: z.string().min(1, "Select a college"),
  major: z.string().min(1, "Select a major, can be undecided"),
  expectedGrad: z
    .number({
      required_error: "Expected graduation year is required",
      invalid_type_error: "Expected graduation year is required",
    })
    .min(2025, "Cannot be less than 2025")
    .max(2032, "Expected graduation cannot be after 7+ years"),
  password: z
    .string()
    .min(6, "Password must be at least 6 characters")
    .max(100, "Password is too long"),
});

type RegisterForm = z.infer<typeof schema>;

const Register = () => {
  const [colleges, setColleges] = useState<string[]>([]);
  const [majors, setMajors] = useState<string[]>([]);
  const [error, setError] = useState("");

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<RegisterForm>({
    resolver: zodResolver(schema),
  });

  const navigate = useNavigate();

  useEffect(() => {
    const getColleges = async () => {
      try {
        const response = await fetchFullColleges();
        setColleges(response.data.sort());
      } catch (err) {
        console.log("Failed to get colleges: ", err);
      }
    };
    getColleges();
  }, []);

  const handleCollegeChange = async (
    e: React.ChangeEvent<HTMLSelectElement>
  ) => {
    const college = e.target.value;
    if (college) {
      try {
        const response = await fetchMajorsByFullCollege(college);
        setMajors(response.data.sort());
      } catch (err) {
        console.log("Failed to fetch majors ", err);
        setMajors([]);
      }
    } else {
      setMajors([]);
    }
  };

  const onSubmit = async (formData: RegisterForm) => {
    try {
      setError(""); // Clear any previous errors
      const res = await api.post("/auth/register", formData);
      console.log(res.data.message);
      localStorage.setItem("verificationEmail", formData.email); // save email so autofills /verify email
      reset();
      navigate("/verify"); // redirect to verification page
    } catch (error: any) {
      const message =
        error.response?.data?.message || "Registration failed. Try again.";
      setError(message);
    }
  };

  return (
    <div
      className="d-flex justify-content-center align-items-center p-5"
      style={{ minHeight: "calc(100vh - 225px)", backgroundColor: "#f5f5f5" }}
    >
      <div className="container" style={{ maxWidth: "480px", width: "100%" }}>
        <h2 className="text-center mb-4">Sign up to start rating BU courses</h2>

        <div className="alert mb-4" role="alert">
          <div className="text-muted text-center">
            Recommendation: Please register using your personal email address,
            as verification codes may not be delivered reliably to university
            email accounts
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="mb-2">
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
          <div className="mb-2">
            <label className="form-label">College:</label>
            <select
              className={`form-select ${errors.college ? "is-invalid" : ""}`}
              defaultValue=""
              {...register("college", {
                onChange: handleCollegeChange, // must include onChange inside
              })}
            >
              <option value="" disabled>
                Select your college
              </option>
              {colleges.map((college) => (
                <option key={college} value={college}>
                  {college}
                </option>
              ))}
            </select>
            {errors.college && (
              <div className="invalid-feedback">{errors.college.message}</div>
            )}
          </div>
          <div className="mb-2">
            <label className="form-label">Major:</label>
            <select
              className={`form-select ${errors.major ? "is-invalid" : ""}`}
              defaultValue=""
              {...register("major")}
              disabled={majors.length === 0}
            >
              <option value="" disabled>
                {majors.length === 0
                  ? "Select your college first"
                  : "Select your major"}
              </option>
              {majors.map((major) => (
                <option key={major} value={major}>
                  {major}
                </option>
              ))}
            </select>
            {errors.major && (
              <div className="invalid-feedback">{errors.major.message}</div>
            )}
          </div>
          <div className="mb-2">
            <label className="form-label">Expected graduation year:</label>
            <input
              className={`form-control ${
                errors.expectedGrad ? "is-invalid" : ""
              }`}
              type="number"
              placeholder="ex: 2027"
              min={2025}
              max={2032}
              {...register("expectedGrad", { valueAsNumber: true })} // HTML returns string otherwise
            ></input>
            {errors.expectedGrad && (
              <div className="invalid-feedback">
                {errors.expectedGrad.message}
              </div>
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
          {error && <div className="text-danger text-center mb-4">{error}</div>}
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
