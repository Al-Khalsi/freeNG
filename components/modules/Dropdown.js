// components/Dropdown.js
import { useState } from 'react';

const Dropdown = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [selected, setSelected] = useState('Category');

    const options = ['html', 'css', 'js', 'react', 'next'];

    const filteredOptions = options.filter(option =>
        option.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const handleOptionClick = (option) => {
        setSelected(option);
        setIsOpen(false);
    };

    return (
        <div className="relative w-52">
            <div
                className="p-2 border border-gray-300 bg-gray-800 text-white cursor-pointer"
                onClick={() => setIsOpen(!isOpen)}
            >
                {selected}
            </div>
            {isOpen && (
                <div className="absolute left-0 right-0 mt-1 border border-gray-300 bg-gray-700 rounded-md shadow-lg z-10">
                    <input
                        type="text"
                        className="w-full p-2 border-b border-gray-300 bg-gray-800 text-white"
                        placeholder="search..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    <div className="max-h-40 overflow-y-auto">
                        {filteredOptions.map(option => (
                            <div
                                key={option}
                                className="p-2 text-white cursor-pointer hover:bg-gray-600"
                                onClick={() => handleOptionClick(option)}
                            >
                                {option}
                            </div>
                        ))}
                    </div>
                </div>
            )}
            {/* بستن منو کشویی هنگام کلیک خارج از آن */}
            <div onClick={() => setIsOpen(false)} className="fixed inset-0 z-0" />
        </div>
    );
};

export default Dropdown;