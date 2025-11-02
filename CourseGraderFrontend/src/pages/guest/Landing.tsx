import { Helmet } from "react-helmet-async";
import LandingImageText from "../../components/LandingImageText";
import AccountPerksGrid from "../../components/AccountPerksGrid";

const Landing = () => {
  return (
    <>
      {/* SEO Metadata */}
      <Helmet>
        <title>
          BU Rate | Boston University Course Reviews and Hub Tracker
        </title>
        <meta
          name="description"
          content="BU Rate helps Boston University students explore 7,500+ courses, read verified reviews, and track Hub requirements all in one place."
        />
        <meta
          name="keywords"
          content="BU Rate, Boston University courses, BU course reviews, BU Hub tracker, BU course search, BU student platform"
        />
        <link rel="canonical" href="https://burate.com/" />

        {/* Social Share Metadata */}
        <meta property="og:type" content="website" />
        <meta
          property="og:title"
          content="BU Rate | Boston University Course Reviews and Hub Tracker"
        />
        <meta
          property="og:description"
          content="Search and review Boston University courses, check Hub credits, and plan your degree with BU Rate."
        />
        <meta property="og:url" content="https://burate.com/" />
        <meta
          property="og:image"
          content="https://burate.com/images/og-banner.png"
        />

        <meta name="twitter:card" content="summary_large_image" />
        <meta
          name="twitter:title"
          content="BU Rate | Boston University Course Reviews and Hub Tracker"
        />
        <meta
          name="twitter:description"
          content="Discover Boston University course insights, ratings, and Hub progress tracking with BU Rate."
        />
        <meta
          name="twitter:image"
          content="https://burate.com/images/og-banner.png"
        />
      </Helmet>

      {/* Page Content */}
      <LandingImageText />
      <AccountPerksGrid />
    </>
  );
};

export default Landing;
