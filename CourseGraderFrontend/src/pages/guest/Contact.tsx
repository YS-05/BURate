import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { sendContactMessage } from "../../api/axios";
import ErrorDisplay from "../../components/ErrorDisplay";

const contactSchema = z.object({
  email: z.string().email("Please enter a valid email address"),
  subject: z
    .string()
    .min(1, "Subject is required")
    .max(200, "Subject must be less than 200 characters"),
  message: z
    .string()
    .min(1, "Message is required")
    .max(3000, "Message must be less than 3000 characters"),
});

type ContactForm = z.infer<typeof contactSchema>;

const Contact = () => {
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<ContactForm>({
    resolver: zodResolver(contactSchema),
  });

  const onSubmit = async (formData: ContactForm) => {
    try {
      setError("");
      setSuccess("");
      const response = await sendContactMessage(formData);
      reset();
      setSuccess(response.message);
    } catch (err: any) {
      console.log(err);
      setError(
        err.response?.data?.message ||
          "Failed to send message. Please try again."
      );
    }
  };

  return (
    <div
      className="d-flex justify-content-center align-items-center"
      style={{ minHeight: "calc(100vh - 225px)", backgroundColor: "#f5f5f5" }}
    >
      <div className="container mt-4 mb-4" style={{ width: "80%" }}>
        <h2 className="text-center mb-4">Contact</h2>
        <p className="text-muted text-center mb-4">
          I'm always eager to hear from BU Rate users! Whether you have
          questions, suggestions for improvements, or just want to share your
          experience, I'd love to connect. Your feedback helps make this
          platform better for the entire BU community.
        </p>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Email Address:</label>
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
            <div className="col-md-6 mb-3">
              <label className="form-label">Subject:</label>
              <input
                type="text"
                className={`form-control ${errors.subject ? "is-invalid" : ""}`}
                {...register("subject")}
              />
              {errors.subject && (
                <div className="invalid-feedback">{errors.subject.message}</div>
              )}
            </div>
            <div className="col-12 mb-3">
              <label className="form-label">Message:</label>
              <textarea
                className={`form-control ${errors.message ? "is-invalid" : ""}`}
                rows={6}
                style={{ resize: "none" }}
                {...register("message")}
              />
              {errors.message && (
                <div className="invalid-feedback">{errors.message.message}</div>
              )}
            </div>
          </div>
          <div className="text-center">
            <button
              type="submit"
              className="btn btn-danger"
              disabled={isSubmitting}
            >
              {isSubmitting ? "Sending..." : "Send Message"}
            </button>
          </div>

          {error && <p className="text-danger mt-2">{error}</p>}
          {success && <p className="text-success mt-2">{success}</p>}
        </form>
        <div className="mt-4 pt-3 text-center">
          <p className="text-muted mb-2">Or reach out to me directly:</p>
          <div className="d-flex justify-content-center gap-3">
            <a
              href="mailto:yash23sharma05@gmail.com"
              className="text-decoration-none text-muted"
            >
              <i className="fas fa-envelope me-1"></i>
              yash23sharma05@gmail.com
            </a>
            <a
              href="https://www.linkedin.com/in/yash-sharma-ys05/"
              target="_blank"
              rel="noopener noreferrer"
              className="text-decoration-none text-muted"
            >
              <i className="fab fa-linkedin me-1"></i>
              LinkedIn
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Contact;
