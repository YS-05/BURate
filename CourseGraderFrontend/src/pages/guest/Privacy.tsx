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
                    Privacy Policy
                  </h1>

                  <div className="text-muted mb-4 text-center">
                    <strong>Effective Date:</strong> August 20, 2025
                    <br />
                    <strong>Last Updated:</strong> August 20, 2025
                  </div>

                  {/* Content */}
                  <div className="privacy-content">
                    {/* existing sections unchanged */}
                    {/* keep your detailed content here exactly as you have it */}
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
