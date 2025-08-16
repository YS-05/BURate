import React, { useEffect, useState } from "react";
import { AccountDTO } from "../../auth/AuthDTOs";
import { useAuth } from "../../auth/AuthProvider";
import {
  fetchFullColleges,
  fetchMajorsByFullCollege,
  getAccount,
  updateAccount,
} from "../../api/axios";
import Spinner from "../../components/Spinner";
import ErrorDisplay from "../../components/ErrorDisplay";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import ResetPassword from "../../components/ResetPassword";

const schema = z.object({
  college: z.string().min(1, "Select a college"),
  major: z.string().min(1, "Select a major"),
  expectedGrad: z
    .number({
      required_error: "Expected graduation year is required",
      invalid_type_error: "Expected graduation year is required",
    })
    .min(2025, "Cannot be less than 2025")
    .max(2035, "Expected graduation cannot be after 2035"),
});

type AccountForm = z.infer<typeof schema>;

const Account = () => {
  const { user } = useAuth();
  const [colleges, setColleges] = useState<string[]>([]);
  const [majors, setMajors] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<AccountForm>({
    resolver: zodResolver(schema),
  });

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const [accountData, collegesResponse] = await Promise.all([
          getAccount(),
          fetchFullColleges(),
        ]);
        setColleges(collegesResponse.data.sort());
        reset({
          expectedGrad: accountData.expectedGrad,
          college: accountData.college,
          major: accountData.major,
        });
        const majorsResponse = await fetchMajorsByFullCollege(
          accountData.college
        );
        setMajors(majorsResponse.data.sort());
      } catch (err: any) {
        console.log(err);
        setError("Failed to fetch user's account details");
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [reset]);

  const handleCollegeChange = async (
    e: React.ChangeEvent<HTMLSelectElement>
  ) => {
    const college = e.target.value;
    if (college) {
      try {
        const response = await fetchMajorsByFullCollege(college);
        setMajors(response.data.sort());
      } catch (err) {
        console.log("Failed to fetch majors", err);
        setMajors([]);
      }
    } else {
      setMajors([]);
    }
  };

  const onSubmit = async (formData: AccountForm) => {
    try {
      setError("");
      await updateAccount(formData);
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to update account");
    }
  };

  if (loading) {
    return <Spinner />;
  }

  if (error) {
    return <ErrorDisplay error={error} />;
  }

  return (
    <div
      className="min-vh-100"
      style={{ backgroundColor: "#f5f5f5", padding: "30px" }}
    >
      <div className="container">
        <h3 className="mb-4 text-center text-danger">Account Settings</h3>
        <div className="card mb-4">
          <div className="card-body border-danger border">
            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <label className="form-label fw-semibold">Email:</label>
              <input
                type="email"
                className="form-control mb-3"
                value={user?.email || ""}
                disabled
              />
              <label className="form-label fw-semibold">College:</label>
              <select
                className="form-select mb-3"
                {...register("college", {
                  onChange: handleCollegeChange,
                })}
              >
                {colleges.map((college) => (
                  <option key={college} value={college}>
                    {college}
                  </option>
                ))}
              </select>
              <label className="form-label fw-semibold">Major:</label>
              <select className="form-select mb-3" {...register("major")}>
                {majors.map((major) => (
                  <option key={major} value={major}>
                    {major}
                  </option>
                ))}
              </select>
              <div className="mb-4">
                <label className="form-label fw-semibold">
                  Expected Graduation Year:
                </label>
                <input
                  className={`form-control ${
                    errors.expectedGrad ? "is-invalid" : ""
                  }`}
                  type="number"
                  placeholder="ex: 2027"
                  min={2025}
                  max={2035}
                  {...register("expectedGrad", { valueAsNumber: true })}
                />
                {errors.expectedGrad && (
                  <div className="invalid-feedback m-0 p-0">
                    {errors.expectedGrad.message}
                  </div>
                )}
              </div>

              <button
                type="submit"
                className="btn btn-danger w-100"
                disabled={isSubmitting}
              >
                {isSubmitting ? "Updating..." : "Update Account"}
              </button>
            </form>
          </div>
        </div>
        <ResetPassword />
      </div>
    </div>
  );
};

export default Account;
