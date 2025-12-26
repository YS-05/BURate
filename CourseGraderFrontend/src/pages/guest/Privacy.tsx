import { Helmet } from "react-helmet-async";
import React from "react";

const Privacy = () => {
  return (
    <>
      {/* SEO Metadata */}
      <Helmet>
        <title>Privacy Policy | BU Rate</title>
        <meta
          name="description"
          content="Learn how BU Rate collects, uses, and protects your information. We are committed to maintaining your privacy and ensuring data transparency for Boston University students."
        />
        <meta
          name="keywords"
          content="BU Rate privacy policy, BU Rate data protection, BU Rate user privacy, Boston University course review privacy, BU Rate security policy"
        />
        <link rel="canonical" href="https://burate.com/privacy" />

        <meta property="og:type" content="article" />
        <meta property="og:title" content="Privacy Policy | BU Rate" />
        <meta
          property="og:description"
          content="Read BU Rate’s privacy policy to understand how your information is collected and protected on our Boston University course review platform."
        />
        <meta property="og:url" content="https://burate.com/privacy" />
        <meta
          property="og:image"
          content="https://burate.com/images/og-banner.png"
        />

        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content="Privacy Policy | BU Rate" />
        <meta
          name="twitter:description"
          content="BU Rate is committed to protecting your privacy and providing a transparent data policy for Boston University students."
        />
        <meta
          name="twitter:image"
          content="https://burate.com/images/og-banner.png"
        />
      </Helmet>

      {/* Page Content */}
      <div>
        <div className="container">
          <div className="row justify-content-center">
            <div className="col-12">
              <div className="card border-0">
                <div className="card-body py-5">
                  <h1 className="bu-red mb-4 text-center">Privacy Policy</h1>

                  <div className="text-muted mb-4 text-center">
                    <strong>Effective Date:</strong> August 20, 2025
                    <br />
                    <strong>Last Updated:</strong> August 20, 2025
                  </div>

                  <div className="privacy-content">
                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">1. Introduction</h2>
                      <p>
                        Welcome to CourseGrader ("we," "our," or "us"). This
                        Privacy Policy explains how we collect, use, disclose,
                        and safeguard your information when you use our course
                        review platform (the "Service").
                      </p>
                      <p>
                        By using our Service, you agree to the collection and
                        use of information in accordance with this Privacy
                        Policy.
                      </p>
                    </section>

                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">
                        2. Information We Collect
                      </h2>

                      <h3 className="h5 mb-2">2.1 Account Information</h3>
                      <p>When you create an account, we collect:</p>
                      <ul>
                        <li>Email address</li>
                        <li>College/University affiliation</li>
                        <li>Major/Field of study</li>
                        <li>Expected graduation year</li>
                        <li>Password (stored securely and encrypted)</li>
                      </ul>

                      <h3 className="h5 mb-2">2.2 Course Activity Data</h3>
                      <p>
                        We collect information about your course interactions:
                      </p>
                      <ul>
                        <li>Courses you've marked as completed</li>
                        <li>Courses you've saved for future reference</li>
                        <li>Courses you've added to your "in progress" list</li>
                        <li>Your course planning and tracking activities</li>
                      </ul>

                      <h3 className="h5 mb-2">2.3 Review Information</h3>
                      <p>When you submit course reviews, we collect:</p>
                      <ul>
                        <li>
                          Course ratings (usefulness, difficulty, workload,
                          interest, teacher score)
                        </li>
                        <li>Professor/instructor names</li>
                        <li>Assignment types and course structure details</li>
                        <li>Semester when you took the course</li>
                        <li>Attendance requirements</li>
                        <li>Estimated hours per week spent on coursework</li>
                        <li>Written review content</li>
                      </ul>
                      <div className="alert alert-info">
                        <strong>Important:</strong> While reviews appear
                        anonymously to other users, we maintain internal records
                        of which user submitted each review for moderation and
                        platform integrity purposes.
                      </div>

                      <h3 className="h5 mb-2">2.4 Usage Data</h3>
                      <p>
                        We automatically collect certain information about your
                        use of our Service:
                      </p>
                      <ul>
                        <li>
                          Log data (IP address, browser type, operating system)
                        </li>
                        <li>Device information</li>
                        <li>Pages visited and features used</li>
                        <li>Time spent on the platform</li>
                        <li>Search queries and filters used</li>
                      </ul>
                    </section>

                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">
                        3. How We Use Your Information
                      </h2>
                      <p>
                        We use the collected information for the following
                        purposes:
                      </p>

                      <h3 className="h5 mb-2">3.1 Core Platform Functions</h3>
                      <ul>
                        <li>Creating and managing your user account</li>
                        <li>
                          Displaying your saved, completed, and in-progress
                          courses
                        </li>
                        <li>Enabling you to submit and view course reviews</li>
                        <li>
                          Personalizing your course recommendations (coming
                          soon)
                        </li>
                        <li>Tracking your academic progress and planning</li>
                      </ul>

                      <h3 className="h5 mb-2">3.2 Platform Improvement</h3>
                      <ul>
                        <li>Analyzing usage patterns to improve our Service</li>
                        <li>Identifying popular courses and trending topics</li>
                        <li>Enhancing search and filtering functionality</li>
                        <li>Developing new features based on user needs</li>
                      </ul>

                      <h3 className="h5 mb-2">3.3 Communication</h3>
                      <ul>
                        <li>Sending important account notifications</li>
                        <li>
                          Responding to your inquiries and support requests
                        </li>
                        <li>
                          Providing updates about platform changes (with your
                          consent)
                        </li>
                      </ul>

                      <h3 className="h5 mb-2">3.4 Safety and Security</h3>
                      <ul>
                        <li>
                          Monitoring for spam, harassment, or inappropriate
                          content
                        </li>
                        <li>
                          Preventing fraud and maintaining platform integrity
                        </li>
                        <li>
                          Enforcing our Terms of Service and Community
                          Guidelines
                        </li>
                      </ul>
                    </section>

                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">
                        4. Information Sharing and Disclosure
                      </h2>

                      <h3 className="h5 mb-2">4.1 Public Information</h3>
                      <ul>
                        <li>
                          Course reviews are displayed anonymously to all users
                        </li>
                        <li>
                          Aggregate course statistics (average ratings, number
                          of reviews) are public
                        </li>
                        <li>
                          Your personal information is never displayed publicly
                        </li>
                      </ul>

                      <h3 className="h5 mb-2">
                        4.2 We Do Not Sell Your Information
                      </h3>
                      <p>
                        We do not sell, trade, or rent your personal information
                        to third parties.
                      </p>

                      <h3 className="h5 mb-2">4.3 Limited Disclosure</h3>
                      <p>
                        We may disclose your information only in the following
                        circumstances:
                      </p>
                      <ul>
                        <li>
                          <strong>Legal Requirements:</strong> When required by
                          law, court order, or government request
                        </li>
                        <li>
                          <strong>Safety Concerns:</strong> To protect the
                          rights, property, or safety of CourseGrader, our
                          users, or others
                        </li>
                        <li>
                          <strong>Business Transfers:</strong> In connection
                          with a merger, acquisition, or sale of assets (users
                          will be notified)
                        </li>
                        <li>
                          <strong>With Your Consent:</strong> When you
                          explicitly authorize us to share specific information
                        </li>
                      </ul>
                    </section>

                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">5. Data Security</h2>
                      <p>
                        We implement appropriate technical and organizational
                        security measures to protect your personal information:
                      </p>
                      <ul>
                        <li>
                          Passwords are encrypted using industry-standard
                          methods
                        </li>
                        <li>Secure data transmission using HTTPS encryption</li>
                        <li>Regular security assessments and updates</li>
                        <li>
                          Limited access to personal data on a need-to-know
                          basis
                        </li>
                      </ul>
                      <p>
                        However, no method of transmission over the internet is
                        100% secure, and we cannot guarantee absolute security.
                      </p>
                    </section>

                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">6. Data Retention</h2>
                      <ul>
                        <li>
                          <strong>Account Information:</strong> Deleted
                          immediately upon account deletion
                        </li>
                        <li>
                          <strong>Course Activity Data:</strong> Deleted
                          immediately upon account deletion
                        </li>
                        <li>
                          <strong>Reviews:</strong> Deleted immediately upon
                          account deletion
                        </li>
                        <li>
                          <strong>Usage Data:</strong> Deleted immediately upon
                          account deletion
                        </li>
                      </ul>
                      <p>
                        When you delete your account, all associated data is
                        permanently removed from our systems and cannot be
                        recovered.
                      </p>
                    </section>

                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">
                        7. Your Rights and Choices
                      </h2>

                      <h3 className="h5 mb-2">7.1 Account Management</h3>
                      <ul>
                        <li>
                          <strong>Access:</strong> View and update your account
                          information at any time
                        </li>
                        <li>
                          <strong>Correction:</strong> Update incorrect or
                          outdated information in your profile
                        </li>
                        <li>
                          <strong>Account Deletion:</strong> Delete your account
                          through your profile settings
                        </li>
                      </ul>

                      <h3 className="h5 mb-2">7.2 Review Management</h3>
                      <ul>
                        <li>
                          <strong>Edit Reviews:</strong> Modify or update your
                          course reviews
                        </li>
                        <li>
                          <strong>Delete Reviews:</strong> Remove reviews you've
                          submitted
                        </li>
                        <li>
                          <strong>Anonymous Display:</strong> Your reviews
                          always appear anonymously to other users
                        </li>
                      </ul>

                      <h3 className="h5 mb-2">7.3 Communication Preferences</h3>
                      <ul>
                        <li>
                          <strong>Email Settings:</strong> Control what types of
                          emails you receive from us
                        </li>
                        <li>
                          <strong>Notifications:</strong> Manage in-app
                          notification preferences
                        </li>
                      </ul>

                      <h3 className="h5 mb-2">7.4 Data Portability</h3>
                      <p>
                        Upon request, we can provide you with a copy of your
                        personal data in a structured, commonly used format.
                      </p>
                    </section>

                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">
                        8. Cookies and Tracking
                      </h2>
                      <p>We use cookies and similar technologies to:</p>
                      <ul>
                        <li>Remember your login status and preferences</li>
                        <li>Analyze how you use the Service</li>
                        <li>
                          Improve platform performance and user experience
                        </li>
                      </ul>
                      <p>
                        You can control cookie preferences through your browser
                        settings, though some features may not function properly
                        if cookies are disabled.
                      </p>
                    </section>

                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">
                        9. Third-Party Services
                      </h2>
                      <p>
                        Our Service may contain links to third-party websites or
                        integrate with external services. This Privacy Policy
                        does not apply to third-party services, and we are not
                        responsible for their privacy practices.
                      </p>
                    </section>

                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">10. Children's Privacy</h2>
                      <p>
                        Our Service is intended for college students and is not
                        designed for children under 13. We do not knowingly
                        collect personal information from children under 13.
                      </p>
                    </section>

                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">
                        11. Changes to This Privacy Policy
                      </h2>
                      <p>
                        We may update this Privacy Policy from time to time. We
                        will notify you of any material changes by:
                      </p>
                      <ul>
                        <li>Posting the updated policy on this page</li>
                        <li>Updating the "Last Updated" date</li>
                        <li>
                          Sending an email notification for significant changes
                          (if you've opted in to receive emails)
                        </li>
                      </ul>
                      <p>
                        Your continued use of the Service after any changes
                        constitutes acceptance of the updated Privacy Policy.
                      </p>
                    </section>

                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">
                        12. Contact Information
                      </h2>
                      <p>
                        If you have any questions, concerns, or requests
                        regarding this Privacy Policy or our data practices,
                        please contact us at:
                      </p>
                      <div className="alert alert-light">
                        <strong>Email:</strong>{" "}
                        <a href="mailto:bucourserate@gmail.com">
                          bucourserate@gmail.com
                        </a>
                        <br />
                        <strong>Subject Line:</strong> Privacy Policy Inquiry
                      </div>
                    </section>

                    <section className="mb-4">
                      <h2 className="h4 bu-red mb-3">
                        13. University Relationship and Project Status
                      </h2>
                      <p>
                        CourseGrader is an independent student-created personal
                        project and is not officially affiliated with Boston
                        University or any other educational institution. This
                        platform was developed as an individual project to help
                        students share course experiences and make informed
                        academic decisions. Course and instructor information is
                        user-generated and reflects individual student
                        experiences.
                      </p>
                    </section>
                  </div>

                  <div className="text-center mt-5 pt-4 border-top">
                    <p className="text-muted">
                      <small>Last updated: August 20, 2025</small>
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Privacy;
