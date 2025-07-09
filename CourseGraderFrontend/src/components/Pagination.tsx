import React from "react";
import "./Pagination.css";

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const Pagination = ({
  currentPage,
  totalPages,
  onPageChange,
}: PaginationProps) => {
  const generatePageNumbers = () => {
    const pages = [];
    const maxPagesToShow = 5;
    if (maxPagesToShow >= totalPages) {
      for (let i = 0; i < totalPages; i++) {
        pages.push(i);
      }
    } else {
      const start = Math.max(0, currentPage - 2);
      const end = Math.min(totalPages - 1, start + maxPagesToShow - 1); // dynamic pagination
      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
    }
    return pages;
  };

  const pageNumbers = generatePageNumbers();

  return (
    <div className="container mb-5">
      <nav aria-label="Course Pagination">
        <ul className="pagination justify-content-center">
          <li className={"page-item " + (currentPage === 0 ? "disabled" : "")}>
            <button
              className="page-link text-danger"
              onClick={() => onPageChange(currentPage - 1)}
              disabled={currentPage === 0}
            >
              Back
            </button>
          </li>

          {pageNumbers[0] > 0 && (
            <>
              <li className="page-item">
                <button
                  className="page-link text-danger"
                  onClick={() => onPageChange(0)}
                >
                  1
                </button>
              </li>
              {pageNumbers[0] > 1 && (
                <li className="page-item disabled">
                  <span className="page-link text-danger">...</span>
                </li>
              )}
            </>
          )}

          {pageNumbers.map((pageNum) => (
            <li
              key={pageNum}
              className={
                "page-item " + (currentPage === pageNum ? "active" : "")
              }
            >
              <button
                className="page-link text-danger"
                onClick={() => onPageChange(pageNum)}
              >
                {pageNum + 1}
              </button>
            </li>
          ))}

          {pageNumbers[pageNumbers.length - 1] < totalPages - 1 && (
            <>
              {pageNumbers[pageNumbers.length - 1] < totalPages - 2 && (
                <li className="page-item disabled">
                  <span className="page-link text-danger">...</span>
                </li>
              )}
              <li className="page-item text-danger">
                <button
                  className="page-link text-danger"
                  onClick={() => onPageChange(totalPages - 1)}
                >
                  {totalPages}
                </button>
              </li>
            </>
          )}

          <li
            className={
              "page-item " + (currentPage === totalPages - 1 ? "disabled" : "")
            }
          >
            <button
              className="page-link text-danger"
              onClick={() => onPageChange(currentPage + 1)}
              disabled={currentPage === totalPages - 1}
            >
              Next
            </button>
          </li>
        </ul>
      </nav>
    </div>
  );
};

export default Pagination;
