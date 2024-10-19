// components/SelectWithSearch.js
import { useState } from 'react';

const SelectWithSearch = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [selected, setSelected] = useState('Category');
    const [search, setSearch] = useState('');

    const options = ['html', 'css', 'js', 'react', 'next'];

    const toggleOptions = () => {
        setIsOpen(!isOpen);
        setSearch(''); // Reset search input when opening
    };

    const filterOptions = (option) => {
        return option.toLowerCase().includes(search.toLowerCase());
    };

    const handleOptionClick = (option) => {
        setSelected(option);
        setIsOpen(false);
    };

    return (
        <div className="relative w-52 mr-6">
            <div 
                className="cursor-pointer border border-gray-300 bg-gray-800 text-white p-2 rounded" 
                onClick={toggleOptions}
            >
                {selected}
            </div>
            {isOpen && (
                <div className="absolute border border-gray-300 bg-gray-700 w-full max-h-40 overflow-y-auto rounded mt-1 z-10">
                    <input 
                        type="text" 
                        className="p-2 w-full bg-gray-800 text-white border-b border-gray-300 rounded-t" 
                        placeholder="search..." 
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                    />
                    <div className="overflow-y-auto max-h-32 scrollbar-thin scrollbar-thumb-yellow-400 scrollbar-track-gray-600">
                        {options.filter(filterOptions).map((option, index) => (
                            <div 
                                key={index} 
                                className="option cursor-pointer p-2 text-white hover:bg-gray-600"
                                onClick={() => handleOptionClick(option)}
                            >
                                {option}
                            </div>
                        ))}
                    </div>
                </div>
            )}
            {/* Close dropdown on outside click */}
            {isOpen && <div className="fixed inset-0 z-0" onClick={() => setIsOpen(false)} />}
            <style jsx>{`
                .scrollbar-thin::-webkit-scrollbar {
                    width: 8px; /* عرض نوار اسکرول */
                }
                .scrollbar-thin::-webkit-scrollbar-thumb {
                    background: yellow; /* رنگ نوار اسکرول */
                    border-radius: 10px; /* گوشه‌های گرد */
                }
                .scrollbar-thin::-webkit-scrollbar-track {
                    background: #34495e; /* رنگ پس‌زمینه نوار اسکرول */
                }
                /* مخفی کردن نوار اسکرول پیش‌فرض */
                .overflow-y-auto::-webkit-scrollbar {
                    display: none; /* مخفی کردن نوار اسکرول پیش‌فرض */
                }
            `}</style>
        </div>
    );
};

export default SelectWithSearch;