import React from "react";

const Terms = () => {
  return (
    <div
      className="min-vh-100"
      style={{
        backgroundColor: "#f5f5f5",
        paddingTop: "2rem",
        paddingBottom: "2rem",
      }}
    >
      <div className="container">
        <div className="row justify-content-center">
          <div className="col-12">
            <div className="card border-0 shadow-sm">
              <div className="card-body p-5">
                <h1 className="text-danger mb-4 text-center">
                  Terms of Service
                </h1>

                <div className="text-muted mb-4 text-center">
                  <strong>Effective Date:</strong> August 20, 2025
                  <br />
                  <strong>Last Updated:</strong> August 20, 2025
                </div>

                <div className="terms-content">
                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      1. Acceptance of Terms
                    </h2>
                    <p>
                      Welcome to CourseGrader ("we," "our," or "us"). These
                      Terms of Service ("Terms") govern your use of our course
                      review platform and related services (collectively, the
                      "Service").
                    </p>
                    <p>
                      By accessing or using CourseGrader, you agree to be bound
                      by these Terms. If you do not agree to these Terms, please
                      do not use our Service.
                    </p>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      2. Description of Service
                    </h2>
                    <p>
                      CourseGrader is a platform that allows Boston University
                      students to:
                    </p>
                    <ul>
                      <li>Share and read course reviews and ratings</li>
                      <li>Track completed, saved, and in-progress courses</li>
                      <li>Access course information and statistics</li>
                      <li>
                        Connect with the BU academic community through shared
                        experiences
                      </li>
                    </ul>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      3. Eligibility and Account Registration
                    </h2>

                    <h3 className="h5 mb-2">3.1 Eligibility</h3>
                    <p>To use CourseGrader, you must:</p>
                    <ul>
                      <li>Be at least 13 years old</li>
                      <li>
                        Be a current or former Boston University student (or
                        prospective student)
                      </li>
                      <li>
                        Provide accurate and complete registration information
                      </li>
                      <li>Maintain the security of your account credentials</li>
                    </ul>

                    <h3 className="h5 mb-2">3.2 Account Responsibility</h3>
                    <p>You are responsible for:</p>
                    <ul>
                      <li>All activities that occur under your account</li>
                      <li>Maintaining the confidentiality of your password</li>
                      <li>
                        Notifying us immediately of any unauthorized use of your
                        account
                      </li>
                      <li>
                        Ensuring your account information remains accurate and
                        up-to-date
                      </li>
                    </ul>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      4. User Content and Conduct
                    </h2>

                    <h3 className="h5 mb-2">4.1 Review Guidelines</h3>
                    <p>When submitting course reviews, you agree to:</p>
                    <ul>
                      <li>
                        Provide honest, accurate, and constructive feedback
                        based on your actual experience
                      </li>
                      <li>
                        Focus on course content, structure, workload, and
                        teaching effectiveness
                      </li>
                      <li>
                        Respect the privacy and dignity of instructors and
                        fellow students
                      </li>
                      <li>
                        Avoid sharing specific exam questions, assignment
                        details, or confidential course materials
                      </li>
                    </ul>

                    <h3 className="h5 mb-2">4.2 Prohibited Content</h3>
                    <div className="alert alert-warning">
                      <strong>You may not submit content that:</strong>
                      <ul className="mb-0 mt-2">
                        <li>
                          Contains false, misleading, or defamatory information
                        </li>
                        <li>
                          Includes personal attacks, harassment, or
                          discriminatory language
                        </li>
                        <li>
                          Violates academic integrity or encourages cheating
                        </li>
                        <li>Infringes on intellectual property rights</li>
                        <li>
                          Contains spam, advertising, or promotional content
                        </li>
                        <li>
                          Includes personal information about instructors or
                          students (beyond course-related experiences)
                        </li>
                        <li>Violates any applicable laws or regulations</li>
                      </ul>
                    </div>

                    <h3 className="h5 mb-2">
                      4.3 Content Ownership and License
                    </h3>
                    <ul>
                      <li>You retain ownership of the content you submit</li>
                      <li>
                        By posting content, you grant us a non-exclusive,
                        royalty-free license to use, modify, and display your
                        content on the platform
                      </li>
                      <li>
                        You represent that you have the right to grant this
                        license
                      </li>
                      <li>
                        We reserve the right to remove content that violates
                        these Terms
                      </li>
                    </ul>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      5. Platform Rules and Restrictions
                    </h2>

                    <h3 className="h5 mb-2">5.1 Acceptable Use</h3>
                    <p>
                      You agree to use CourseGrader only for its intended
                      purposes and will not:
                    </p>
                    <ul>
                      <li>
                        Attempt to gain unauthorized access to our systems or
                        other users' accounts
                      </li>
                      <li>
                        Use automated tools (bots, scrapers) to access or
                        collect data from the Service
                      </li>
                      <li>
                        Interfere with or disrupt the platform's functionality
                      </li>
                      <li>
                        Create multiple accounts to manipulate ratings or
                        circumvent restrictions
                      </li>
                      <li>
                        Use the Service for any commercial purposes without our
                        permission
                      </li>
                    </ul>

                    <h3 className="h5 mb-2">5.2 Academic Integrity</h3>
                    <div className="alert alert-info">
                      <strong>
                        CourseGrader is designed to help students make informed
                        course decisions, not to:
                      </strong>
                      <ul className="mb-0 mt-2">
                        <li>Facilitate cheating or academic dishonesty</li>
                        <li>
                          Share specific exam content or assignment solutions
                        </li>
                        <li>
                          Encourage students to avoid challenging but
                          educationally valuable courses solely based on
                          difficulty
                        </li>
                      </ul>
                    </div>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      6. Content Moderation and Enforcement
                    </h2>

                    <h3 className="h5 mb-2">6.1 Monitoring</h3>
                    <p>We reserve the right, but are not obligated, to:</p>
                    <ul>
                      <li>Monitor user content and activity</li>
                      <li>Remove content that violates these Terms</li>
                      <li>Suspend or terminate accounts for violations</li>
                      <li>
                        Cooperate with educational institutions regarding policy
                        violations
                      </li>
                    </ul>

                    <h3 className="h5 mb-2">6.2 Reporting</h3>
                    <p>
                      Users can report inappropriate content or behavior. We
                      will investigate reports and take appropriate action,
                      which may include:
                    </p>
                    <ul>
                      <li>Content removal</li>
                      <li>User warnings</li>
                      <li>Account suspension or termination</li>
                      <li>Reporting to relevant authorities if necessary</li>
                    </ul>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      7. Privacy and Data Protection
                    </h2>
                    <p>
                      Your privacy is important to us. Our collection and use of
                      personal information is governed by our
                      <a href="/privacy" className="text-danger">
                        {" "}
                        Privacy Policy
                      </a>
                      , which is incorporated into these Terms by reference. By
                      using the Service, you consent to our data practices as
                      described in the Privacy Policy.
                    </p>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      8. Disclaimers and Limitations
                    </h2>

                    <h3 className="h5 mb-2">8.1 Service Availability</h3>
                    <ul>
                      <li>
                        CourseGrader is provided "as is" without warranties of
                        any kind
                      </li>
                      <li>
                        We do not guarantee uninterrupted or error-free service
                      </li>
                      <li>
                        We may modify, suspend, or discontinue the Service at
                        any time
                      </li>
                    </ul>

                    <h3 className="h5 mb-2">8.2 Content Accuracy</h3>
                    <ul>
                      <li>
                        Course reviews reflect individual user experiences and
                        opinions
                      </li>
                      <li>
                        We do not verify the accuracy of user-submitted content
                      </li>
                      <li>
                        Course information may become outdated as curricula
                        change
                      </li>
                      <li>
                        Users should verify course details with official
                        university sources
                      </li>
                    </ul>

                    <h3 className="h5 mb-2">8.3 Educational Decisions</h3>
                    <ul>
                      <li>
                        CourseGrader is a supplementary resource for course
                        selection
                      </li>
                      <li>
                        We are not responsible for academic decisions made based
                        on platform content
                      </li>
                      <li>
                        Students should consult with academic advisors for
                        official guidance
                      </li>
                    </ul>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      9. Limitation of Liability
                    </h2>
                    <p>To the fullest extent permitted by law:</p>
                    <ul>
                      <li>
                        We shall not be liable for any indirect, incidental,
                        special, or consequential damages
                      </li>
                      <li>
                        We are not liable for any damages resulting from your
                        use of or inability to use the Service
                      </li>
                    </ul>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      10. Intellectual Property
                    </h2>

                    <h3 className="h5 mb-2">10.1 Platform Rights</h3>
                    <p>
                      CourseGrader and its original content, features, and
                      functionality are owned by us and are protected by
                      copyright, trademark, and other intellectual property
                      laws.
                    </p>

                    <h3 className="h5 mb-2">10.2 User Rights</h3>
                    <p>
                      We respect intellectual property rights and expect users
                      to do the same. If you believe your intellectual property
                      has been infringed, please contact us at
                      bucourserate@gmail.com.
                    </p>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">11. Termination</h2>

                    <h3 className="h5 mb-2">11.1 Your Right to Terminate</h3>
                    <p>
                      You may terminate your account at any time through your
                      account settings. Upon termination, your access to the
                      Service will cease immediately, and all your data will be
                      deleted as described in our Privacy Policy.
                    </p>

                    <h3 className="h5 mb-2">11.2 Our Right to Terminate</h3>
                    <p>
                      We may terminate or suspend your account immediately,
                      without prior notice, if you:
                    </p>
                    <ul>
                      <li>Violate these Terms</li>
                      <li>
                        Engage in behavior that harms the platform or other
                        users
                      </li>
                      <li>Provide false information during registration</li>
                      <li>Remain inactive for an extended period</li>
                    </ul>

                    <h3 className="h5 mb-2">11.3 Effect of Termination</h3>
                    <p>Upon termination:</p>
                    <ul>
                      <li>Your right to use the Service ceases immediately</li>
                      <li>
                        You remain liable for any obligations incurred before
                        termination
                      </li>
                      <li>
                        Provisions of these Terms that should survive
                        termination will remain in effect
                      </li>
                    </ul>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      12. Changes to Terms
                    </h2>
                    <p>
                      We may modify these Terms from time to time. We will
                      notify you of material changes by:
                    </p>
                    <ul>
                      <li>Posting the updated Terms on this page</li>
                      <li>Updating the "Last Updated" date</li>
                      <li>
                        Sending an email notification for significant changes
                        (if you've opted in)
                      </li>
                    </ul>
                    <p>
                      Your continued use of the Service after changes
                      constitutes acceptance of the updated Terms.
                    </p>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      13. Governing Law and Disputes
                    </h2>

                    <h3 className="h5 mb-2">13.1 Governing Law</h3>
                    <p>
                      These Terms are governed by and construed in accordance
                      with the laws of Massachusetts, without regard to conflict
                      of law principles.
                    </p>

                    <h3 className="h5 mb-2">13.2 Dispute Resolution</h3>
                    <p>
                      Any disputes arising from these Terms or your use of the
                      Service should first be addressed through direct
                      communication with us. If informal resolution is not
                      possible, disputes will be resolved through binding
                      arbitration in Massachusetts.
                    </p>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      14. Contact Information
                    </h2>
                    <p>
                      If you have any questions about these Terms, please
                      contact us at:
                    </p>
                    <div className="alert alert-light">
                      <strong>Email:</strong>{" "}
                      <a href="mailto:bucourserate@gmail.com">
                        bucourserate@gmail.com
                      </a>
                      <br />
                      <strong>Subject Line:</strong> Terms of Service Inquiry
                    </div>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">
                      15. Student Project Disclaimer
                    </h2>
                    <div className="alert alert-primary">
                      <strong>Important Notice:</strong> CourseGrader is an
                      independent student-created personal project and is not
                      officially affiliated with Boston University or any other
                      educational institution. This platform was developed as an
                      individual project to help students share course
                      experiences and make informed academic decisions.
                      <br />
                      <br />
                      While we strive to maintain a reliable and helpful
                      service, users should understand that this is a student
                      project and should verify important course information
                      through official university channels.
                    </div>
                  </section>

                  <section className="mb-4">
                    <h2 className="h4 text-danger mb-3">16. Acknowledgment</h2>
                    <p>
                      By using CourseGrader, you acknowledge that you have read,
                      understood, and agree to be bound by these Terms of
                      Service.
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
  );
};

export default Terms;
