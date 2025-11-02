import { Helmet } from "react-helmet-async";
import React from "react";

const Terms = () => {
  return (
    <>
      {/* SEO Metadata */}
      <Helmet>
        <title>Terms of Service | BU Rate</title>
        <meta
          name="description"
          content="Read the Terms of Service for BU Rate, Boston University’s course review platform. Learn about acceptable use, content policies, and user rights."
        />
        <meta
          name="keywords"
          content="BU Rate terms of service, BU Rate policies, Boston University course reviews rules, BU Rate user agreement, BU Rate legal terms"
        />
        <link rel="canonical" href="https://burate.com/terms" />

        <meta property="og:type" content="website" />
        <meta property="og:title" content="Terms of Service | BU Rate" />
        <meta
          property="og:description"
          content="Understand your rights and responsibilities when using BU Rate, the Boston University course review and planning platform."
        />
        <meta property="og:url" content="https://burate.com/terms" />
        <meta
          property="og:image"
          content="https://burate.com/images/og-banner.png"
        />

        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content="Terms of Service | BU Rate" />
        <meta
          name="twitter:description"
          content="Review BU Rate’s Terms of Service and learn about account use, content policies, and privacy standards."
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
                    Terms of Service
                  </h1>

                  <div className="text-muted mb-4 text-center">
                    <strong>Effective Date:</strong> August 20, 2025
                    <br />
                    <strong>Last Updated:</strong> August 20, 2025
                  </div>

                  {/* Original Terms content below */}
                  <div className="terms-content">
                    {/* ... all your existing sections ... */}
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

export default Terms;
