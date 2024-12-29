import React from 'react';

const Input = React.forwardRef(({ type, value, onChange, placeholder, className, required, autoComplete, checked }, ref) => {
  if (type === 'checkbox') {
    return (
      <input
        type="checkbox"
        checked={checked}
        onChange={onChange}
        className={className}
      />
    );
  }

  if (type === 'file') {
    return (
      <input
        type="file"
        onChange={onChange}
        className={className}
        ref={ref}
      />
    );
  }

  return (
    <input
      type={type}
      value={value}
      onChange={onChange}
      placeholder={placeholder}
      className={`${className}`}
      required={required}
      autoComplete={autoComplete}
      ref={ref}
    />
  );
});

export default Input;