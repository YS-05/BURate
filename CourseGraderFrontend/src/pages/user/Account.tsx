import React, { useEffect, useState } from "react";
import { AccountDTO } from "../../auth/AuthDTOs";
import { useAuth } from "../../auth/AuthProvider";
import {
  fetchFullColleges,
  fetchMajorsByFullCollege,
  getAccount,
  updateAccount,
  deleteUser,
} from "../../api/axios";
import Spinner from "../../components/Spinner";
import ErrorDisplay from "../../components/ErrorDisplay";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import ResetPassword from "../../components/ResetPassword";
import { useNavigate } from "react-router-dom";

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
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const [colleges, setColleges] = useState<string[]>([]);
  const [majors, setMajors] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [isDeletingAccount, setIsDeletingAccount] = useState(false);

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

  const handleDeleteAccount = async () => {
    const confirmed = window.confirm(
      "Are you sure you want to delete your account?\n\nThis will permanently delete:\n• Your profile information\n• All your course reviews\n• All your saved courses\n• All your course progress\n\nThis action CANNOT be undone!"
    );
    if (!confirmed) return;
    setIsDeletingAccount(true);
    try {
      await deleteUser();
      localStorage.removeItem("token");
      logout();
      navigate("/");
    } catch (err: any) {
      console.error("Error deleting account:", err);

      if (err.response?.status === 401) {
        alert(
          err.response?.data?.error ||
            "Failed to delete account. Please try again."
        );
      }
    } finally {
      setIsDeletingAccount(false);
    }
  };

  if (loading) {
    return <Spinner />;
  }

  if (error) {
    return <ErrorDisplay error={error} />;
  }

  return (
    <div className="container my-5">
      <div className="mx-auto" style={{ maxWidth: "720px" }}>
        <h1 className="mb-5 fw-bold">Account Settings</h1>
        <div className="card mb-5">
          <div className="card-body border border-dark rounded-2">
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
                className="btn btn-bu-red w-100"
                disabled={isSubmitting}
              >
                {isSubmitting ? "Updating..." : "Update Account"}
              </button>
            </form>
          </div>
        </div>
        <ResetPassword />
        <div className="mt-5">
          <h4 className="mb-3 fw-bold">Danger Zone</h4>
          <button
            type="button"
            className="btn btn-outline-bu-red w-100"
            onClick={handleDeleteAccount}
            disabled={isDeletingAccount}
          >
            {isDeletingAccount ? "Deleting Account..." : "Delete My Account"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default Account;
