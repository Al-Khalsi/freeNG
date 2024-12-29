// components/Pagination.js
import React from 'react';

const Pagination = ({ currentPage, totalPages, onPageChange }) => {
    const renderPagination = () => {
        const pagination = [];
        const maxVisiblePages = 5;

        let startPage, endPage;
        if (totalPages <= maxVisiblePages) {
            startPage = 1;
            endPage = totalPages;
        } else {
            const middlePage = Math.ceil(maxVisiblePages / 2);
            if (currentPage <= middlePage) {
                startPage = 1;
                endPage = maxVisiblePages;
            } else if (currentPage + middlePage - 1 >= totalPages) {
                startPage = totalPages - maxVisiblePages + 1;
                endPage = totalPages;
            } else {
                startPage = currentPage - middlePage + 1;
                endPage = currentPage + middlePage - 1;
            }
        }

        if (startPage > 1) {
            pagination.push(
                <button key="first" onClick={() => onPageChange(1)} className="mx-2 px-4 py-2 rounded-lg bg-bgDarkGray hover:bg-bgDarkGray2">
                    First
                </button>
            );
            if (startPage > 2) {
                pagination.push(<span key="ellipsis-start">...</span>);
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            pagination.push(
                <button
                    key={i}
                    onClick={() => onPageChange(i)}
                    className={`mx-2 px-4 py-2 rounded-lg 
                    ${currentPage === i ? 'bg-gradient-to-t from-bgLightPurple to-bgPurple text-white' : 'bg-bgDarkGray hover:bg-bgDarkGray2'}`}>
                    {i}
                </button>
            );
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                pagination.push(<span key="ellipsis-end">...</span>);
            }
            pagination.push(
                <button key="last" onClick={() => onPageChange(totalPages)}
                    className="mx-2 px-4 py-2 rounded-lg bg-bgDarkGray hover:bg-bgDarkGray2">
                    Last
                </button>
            );
        }

        return pagination;
    };

    return (
        <div className="pagination flex justify-center py-4">
            {renderPagination()}
        </div>
    );
};

export default Pagination;