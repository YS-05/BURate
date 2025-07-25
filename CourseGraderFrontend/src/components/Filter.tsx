import React, { useEffect, useState } from "react";
import Select, { components, OptionProps } from "react-select";
import { fetchColleges, fetchDepartmentsByCollege } from "../api/axios";

interface OptionType {
  label: string;
  value: string;
}

interface FilterProps {
  setFilters: (filters: Record<string, any>) => void;
  onSearch: (filters: Record<string, any>) => void;
  setSortBy: (sortBy: string) => void;
  sortBy: string;
}

const hubRequirementOptions: OptionType[] = [
  { label: "Aesthetic Exploration (AEX)", value: "AEX" },
  { label: "Philosophical Inquiry and Life's Meanings (PLM)", value: "PLM" },
  { label: "Historical Consciousness (HCO)", value: "HCO" },
  { label: "Scientific Inquiry I (SI1)", value: "SI1" },
  { label: "Social Inquiry I (SO1)", value: "SO1" },
  { label: "Scientific Inquiry II (SI2)", value: "SI2" },
  { label: "Social Inquiry II (SO2)", value: "SO2" },
  { label: "Quantitative Reasoning I (QR1)", value: "QR1" },
  { label: "Quantitative Reasoning II (QR2)", value: "QR2" },
  { label: "The Individual in Community (IIC)", value: "IIC" },
  {
    label: "Global Citizenship and Intercultural Literacy (GCI)",
    value: "GCI",
  },
  { label: "Ethical Reasoning (ETR)", value: "ETR" },
  { label: "First-Year Writing Seminar (FYW)", value: "FYW" },
  { label: "Writing, Research, and Inquiry (WRI)", value: "WRI" },
  { label: "Writing-Intensive Course (WIN)", value: "WIN" },
  { label: "Oral and/or Signed Communication (OSC)", value: "OSC" },
  { label: "Digital/Multimedia Expression (DME)", value: "DME" },
  { label: "Critical Thinking (CRT)", value: "CRT" },
  { label: "Research and Information Literacy (RIL)", value: "RIL" },
  { label: "Teamwork/Collaboration (TWC)", value: "TWC" },
  { label: "Creativity/Innovation (CRI)", value: "CRI" },
];

const CheckboxOption = (props: OptionProps<OptionType, true>) => (
  <components.Option {...props}>
    <input
      type="checkbox"
      checked={props.isSelected}
      onChange={() => null}
      style={{ marginRight: 8 }}
    />
    {props.label}
  </components.Option>
);

const Filter = ({ setFilters, onSearch, setSortBy, sortBy }: FilterProps) => {
  const [collegeOptions, setCollegeOptions] = useState<OptionType[]>([]);
  const [selectedColleges, setSelectedColleges] = useState<OptionType[]>([]);
  const [departmentOptions, setDepartmentOptions] = useState<OptionType[]>([]);
  const [selectedDepartments, setSelectedDepartments] = useState<OptionType[]>(
    []
  );
  const [selectedHubs, setSelectedHubs] = useState<OptionType[]>([]);

  const [minRating, setMinRating] = useState(0);
  const [maxDifficulty, setMaxDifficulty] = useState(5);
  const [maxWorkload, setMaxWorkload] = useState(5);
  const [minUsefulness, setMinUsefulness] = useState(0);
  const [minInterest, setMinInterest] = useState(0);
  const [minTeacher, setMinTeacher] = useState(0);
  const [minCourseCode, setMinCourseCode] = useState<number | "">("");
  const [reviewCount, setReviewCount] = useState<number | "">("");
  const [noPreReqs, setNoPreReqs] = useState(false);

  useEffect(() => {
    const loadColleges = async () => {
      try {
        const res = await fetchColleges();
        setCollegeOptions(
          res.data.map((c: string) => ({ value: c, label: c }))
        );
      } catch (err) {
        console.log("Error fetching colleges:", err);
      }
    };
    loadColleges();
  }, []);

  useEffect(() => {
    const loadDepartments = async () => {
      if (selectedColleges.length == 0) {
        setDepartmentOptions([]);
        setSelectedDepartments([]);
        return;
      }
      const deptSet = new Set<string>();
      for (const college of selectedColleges) {
        try {
          const res = await fetchDepartmentsByCollege(college.value);
          res.data.forEach((d: string) => deptSet.add(d));
        } catch (err) {
          console.log("Error fetching departments:", err);
        }
      }
      setDepartmentOptions(
        Array.from(deptSet).map((d) => ({ value: d, label: d }))
      );
      setSelectedDepartments([]);
    };
    loadDepartments();
  }, [selectedColleges]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const payload = {
      colleges: selectedColleges.map((c) => c.value),
      departments: selectedDepartments.map((d) => d.value),
      hubReqs: selectedHubs.map((h) => h.value),
      minRating,
      maxDifficulty,
      maxWorkload,
      minUsefulness,
      minInterest,
      minTeacher,
      minCourseCode: minCourseCode === "" ? undefined : minCourseCode,
      reviewCount: reviewCount === "" ? undefined : reviewCount,
      noPreReqs,
      sortBy: sortBy || undefined, // Include sortBy in the payload
    };

    setFilters(payload);
    onSearch(payload);
  };

  return (
    <form className="container" onSubmit={handleSubmit}>
      <h3 className="mb-4 text-center">Filter Courses</h3>
      <div className="mb-1">
        <h5 className="mb-3">Initial Filters</h5>
        <div className="row">
          <div className="col-12 col-md-6 col-lg-4 mb-3">
            <label className="form-label">Colleges</label>
            <Select
              options={collegeOptions}
              value={selectedColleges}
              onChange={(opts) => setSelectedColleges(opts as OptionType[])}
              isMulti
              closeMenuOnSelect={false}
              placeholder="Select colleges..."
              components={{ Option: CheckboxOption }}
            />
          </div>
          <div className="col-12 col-md-6 col-lg-4 mb-3">
            <label className="form-label">Departments</label>
            <Select
              options={departmentOptions}
              value={selectedDepartments}
              onChange={(opts) => setSelectedDepartments(opts as OptionType[])}
              isMulti
              closeMenuOnSelect={false}
              isDisabled={departmentOptions.length === 0}
              placeholder="Select departments..."
              components={{ Option: CheckboxOption }}
            />
          </div>
          <div className="col-12 col-lg-4 mb-3">
            <label className="form-label">Hub Requirements</label>
            <Select
              options={hubRequirementOptions}
              value={selectedHubs}
              onChange={(opts) => setSelectedHubs(opts as OptionType[])}
              isMulti
              closeMenuOnSelect={false}
              placeholder="Select hub requirements..."
              components={{ Option: CheckboxOption }}
            />
          </div>
        </div>
        <div className="row">
          <div className="col-12 col-md-4 col-lg-4 mb-3">
            <label className="form-label">Minimum Course Number</label>
            <input
              type="number"
              className="form-control"
              placeholder="e.g. 100"
              min={0}
              value={minCourseCode}
              onChange={(e) =>
                setMinCourseCode(
                  e.target.value === "" ? "" : Number(e.target.value)
                )
              }
            />
          </div>
          <div className="col-12 col-md-4 col-lg-4 mb-3">
            <label className="form-label">Minimum Reviews</label>
            <input
              type="number"
              className="form-control"
              placeholder="e.g. 5"
              min={0}
              value={reviewCount}
              onChange={(e) =>
                setReviewCount(
                  e.target.value === "" ? "" : Number(e.target.value)
                )
              }
            />
          </div>
          <div className="col-12 col-md-4 col-lg-4 mb-3">
            <label className="form-label">Prerequisites Required</label>
            <select
              className="form-select"
              value={noPreReqs ? "noPrereqsOnly" : "any"}
              onChange={(e) => setNoPreReqs(e.target.value === "noPrereqsOnly")}
            >
              <option value="any">Any</option>
              <option value="noPrereqsOnly">No prerequisites</option>
            </select>
          </div>
        </div>
        <div className="row">
          <h5 className="mb-3">Rating sliders</h5>
          <div className="col-12 col-md-4 col-lg-2 mb-3">
            <label className="form-label">
              Min Overall Rating: {minRating.toFixed(1)}
            </label>
            <input
              type="range"
              className="form-range"
              min={0}
              max={5}
              step={0.1}
              value={minRating}
              onChange={(e) => setMinRating(parseFloat(e.target.value))}
            />
          </div>
          <div className="col-12 col-md-4 col-lg-2 mb-3">
            <label className="form-label">
              Min Teacher: {minTeacher.toFixed(1)}
            </label>
            <input
              type="range"
              className="form-range"
              min={0}
              max={5}
              step={0.1}
              value={minTeacher}
              onChange={(e) => setMinTeacher(parseFloat(e.target.value))}
            />
          </div>
          <div className="col-12 col-md-4 col-lg-2 mb-3">
            <label className="form-label">
              Min Usefulness: {minUsefulness.toFixed(1)}
            </label>
            <input
              type="range"
              className="form-range"
              min={0}
              max={5}
              step={0.1}
              value={minUsefulness}
              onChange={(e) => setMinUsefulness(parseFloat(e.target.value))}
            />
          </div>
          <div className="col-12 col-md-4 col-lg-2 mb-3">
            <label className="form-label">
              Min Interest: {minInterest.toFixed(1)}
            </label>
            <input
              type="range"
              className="form-range"
              min={0}
              max={5}
              step={0.1}
              value={minInterest}
              onChange={(e) => setMinInterest(parseFloat(e.target.value))}
            />
          </div>
          <div className="col-12 col-md-4 col-lg-2 mb-3">
            <label className="form-label">
              Max Difficulty: {maxDifficulty}
            </label>
            <input
              type="range"
              className="form-range"
              min={1}
              max={5}
              step={1}
              value={maxDifficulty}
              onChange={(e) => setMaxDifficulty(Number(e.target.value))}
            />
          </div>
          <div className="col-12 col-md-4 col-lg-2 mb-3">
            <label className="form-label">Max Workload: {maxWorkload}</label>
            <input
              type="range"
              className="form-range"
              min={1}
              max={5}
              step={1}
              value={maxWorkload}
              onChange={(e) => setMaxWorkload(Number(e.target.value))}
            />
          </div>
        </div>
        <div className="row">
          <div className="col-12 col-md-3 col-lg-2 mb-4">
            <label className="form-label">Sort Results By</label>
            <select
              className="form-select"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
            >
              <option value="byCourseCode">Course Number</option>
              <option value="byRating">Best Rating</option>
              <option value="byReviews">Most Reviewed</option>
            </select>
          </div>
          <div className="col-12 col-md-6 col-lg-4 mb-3 d-flex align-items-end">
            <button
              type="submit"
              className="btn btn-danger"
              style={{
                letterSpacing: "0.5px",
                padding: "0.6rem 1.4rem",
                fontWeight: 500,
                fontSize: "1.05rem",
              }}
            >
              Search Courses
            </button>
          </div>
        </div>
      </div>
    </form>
  );
};

export default Filter;
