import React, { useState, forwardRef, useEffect } from 'react';
import Button from '@/components/modules/Button';
import Input from '@/components/modules/Input';

const Selector = forwardRef(({ options, selectedOptions, onChange, title }, ref) => {
  const [showDropdown, setShowDropdown] = useState(false);

  const handleOptionChange = (option) => {
    onChange(option);
  };

  const toggleDropdown = () => {
    setShowDropdown(prev => !prev);
  };

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (ref.current && !ref.current.contains(event.target)) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [ref]);

  return (
    <div className="relative mx-2 w-full sm:w-1/2" ref={ref}>
      <Button
        type="button"
        onClick={toggleDropdown}
        className="flex border rounded px-3 py-2 w-full bg-bgDarkGray2"
      >
        {selectedOptions.length > 0 ? selectedOptions.join(', ') : `Select ${title}`}
      </Button>
      {showDropdown && (
        <div className='absolute bg-bgDarkGray2 border border-t-0 rounded w-full z-10'>
          <div style={{ maxHeight: '200px', overflowY: 'auto' }}>
            {options.map((option) => (
              <label key={option.name || option} className="flex justify-between items-center p-2 border-b cursor-pointer hover:bg-bgDarkGray">
                <span className="flex items-center">
                  {option.hex && (
                    <span
                      className="block w-4 h-4 rounded-full mr-2"
                      style={{ backgroundColor: option.hex }}
                    ></span>
                  )}
                  {option.name || option}
                </span>
                <Input
                  type="checkbox"
                  value={option.name || option}
                  checked={selectedOptions.includes(option.name || option)}
                  onChange={() => handleOptionChange(option.name || option)}
                  className="mr-2"
                />
              </label>
            ))}
          </div>
        </div>
      )}
    </div>
  );
});

export default Selector;