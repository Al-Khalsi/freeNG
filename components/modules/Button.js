import React from 'react';

const Button = ({ type, onClick, className, disabled, children }) => {
  return (
    <button
      type={type}
      onClick={onClick}
      className={`rounded p-2 ${className}`}
      disabled={disabled}
    >
      {children}
    </button>
  );
};

export default Button;