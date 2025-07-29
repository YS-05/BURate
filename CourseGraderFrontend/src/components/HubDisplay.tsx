import React from "react";
import { HubRequirementDTO } from "../auth/AuthDTOs";

interface Props {
  hubs: HubRequirementDTO[] | undefined;
}

const HubDisplay = ({ hubs }: Props) => {
  return (
    <div className="card border-danger rounded-0 mb-4">
      <div className="card-header">
        <h5 className="mb-0">Hub Requirements Fulfilled</h5>
      </div>
      <div className="card-body">
        {hubs && hubs?.length > 0 ? (
          <div className="d-flex flex-wrap gap-2">
            {hubs.map((hub, index) => (
              <span
                key={index}
                className="badge bg-danger-subtle text-danger-emphasis border fs-6 px-3 py-2"
              >
                {hub.name}
              </span>
            ))}
          </div>
        ) : (
          <p className="text-muted mb-0">
            This course does not fulfill any hub requirements.
          </p>
        )}
      </div>
    </div>
  );
};

export default HubDisplay;
