import React from "react";

const features = [
  {
    icon: "search",
    title: "Smart Course Search",
    desc: "Quickly find courses by typing the course code, name, or various filters.",
  },
  {
    icon: "graph-up-arrow",
    title: "Verified Reviews",
    desc: "All reviews are fully guarenteed to be from BU Students",
  },
  {
    icon: "shield",
    title: "Anonymous Reviews",
    desc: "Share honest feedback anonymously. Your privacy is our priority.",
  },
];

const WhyChoose = () => {
  return (
    <div className="container my-5 pb-5">
      <h2 className="text-center fw-bold mb-3 display-5">
        Everything You Need to <span className="bu-red">Choose Wisely</span>
      </h2>
      <p className="text-center mb-5">
        Built for students. We understand what matters when picking your
        courses.
      </p>
      {/* Grid for features */}
      <div className="row g-5">
        {features.map((feature) => (
          <div className="col-md-4">
            <div className="border rounded-3 px-4 py-4">
              <i className={`bi bi-${feature.icon} bu-red fs-3`} />
              <h5 className="fw-bold mt-3">{feature.title}</h5>
              <p className="text-muted mb-0">{feature.desc}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default WhyChoose;
