import { Link } from "react-router-dom";
import HubImg from "../assets/hubimg.png";
import ReviewImg from "../assets/reviewperson.png";
import addCourseImg from "../assets/addRev.png";
import SearchImg from "../assets/Searchbar.png";
import BookImg from "../assets/Book.png";
import TeacherImg from "../assets/teacher.png";

const perks = [
  {
    title: "Track Hub Progress",
    description:
      "Get a visual graph which allows you to track your progress for each hub requirement",
    image: HubImg,
  },
  {
    title: "Post Anonymous Reviews",
    description:
      "Add reviews completely anonymous to help future students make better and more informed choices",
    image: ReviewImg,
  },
  {
    title: "Quickly Add and Remove Courses",
    description:
      "Check how the addition or removal of a course affects overall credits and hub progress",
    image: addCourseImg,
  },
];

const whyUs = [
  {
    title: "Advanced Search Options",
    description:
      "Search for courses based on various advanced features (ex: hubs, pre-reqs, ratings)",
    image: SearchImg,
  },
  {
    title: "Read past opinions",
    description:
      "Read reviews from past students to make more well informed decisions when picking courses",
    image: BookImg,
  },
  {
    title: "Understand teaching styles",
    description:
      "Quickly sort reviews by professor after selecting a course, see which teaching style fits",
    image: TeacherImg,
  },
];

const AccountPerksGrid = () => {
  return (
    <div
      className="container-fluid py-5"
      style={{ backgroundColor: "#f5f5f5" }}
    >
      <div className="container">
        <h2 className="text-center fw-bold mb-5">Why use BU Rate?</h2>

        <div className="row row-cols-1 row-cols-md-3 g-4 mb-5">
          {whyUs.map((perk, index) => (
            <div className="col d-flex" key={index}>
              <div
                className="card shadow-sm flex-fill border border-danger text-center"
                style={{ borderWidth: "2px" }}
              >
                <div className="card-body d-flex flex-column align-items-center">
                  <img
                    src={perk.image}
                    alt={perk.title}
                    className="mb-3"
                    style={{
                      width: "180px",
                      height: "180px",
                      objectFit: "contain",
                    }}
                  />
                  <h5 className="card-title">{perk.title}</h5>
                  <p className="card-text mt-auto text-muted">
                    {perk.description}
                  </p>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="text-center my-5">
          <Link to="/register" className="btn btn-danger btn-lg px-5">
            Get Started Now
          </Link>
        </div>

        <h2 className="text-center fw-bold mb-5">
          Unlock These Features by Creating a Free Account
        </h2>

        <div className="row row-cols-1 row-cols-md-3 g-4">
          {perks.map((perk, index) => (
            <div className="col d-flex" key={index}>
              <div
                className="card shadow-sm flex-fill border border-danger text-center"
                style={{ borderWidth: "2px" }}
              >
                <div className="card-body d-flex flex-column align-items-center">
                  <img
                    src={perk.image}
                    alt={perk.title}
                    className="mb-3"
                    style={{
                      width: "180px",
                      height: "180px",
                      objectFit: "contain",
                    }}
                  />
                  <h5 className="card-title">{perk.title}</h5>
                  <p className="card-text mt-auto text-muted">
                    {perk.description}
                  </p>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="text-center mt-5">
          <Link to="/register" className="btn btn-danger btn-lg px-5">
            Get Started Now
          </Link>
        </div>
      </div>
    </div>
  );
};

export default AccountPerksGrid;
