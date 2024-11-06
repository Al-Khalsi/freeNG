import SelectWithSearch from '../modules/SelectWithSearch';

const Aside = ({ openSelect, handleSelectToggle, options }) => {
    return (
        <aside className='filter-sidebar flex flex-col w-1/6 bg-bgDarkGray2 rounded-2xl mr-8'>
            <div className='filtering'>
                <SelectWithSearch
                    options={options[0]}
                    defaultText="Categories"
                    isOpen={openSelect === 1}
                    onToggle={() => handleSelectToggle(1)}
                />
                <SelectWithSearch
                    options={options[1]}
                    defaultText="Sub Categories"
                    isOpen={openSelect === 2}
                    onToggle={() => handleSelectToggle(2)}
                />
                <SelectWithSearch
                    options={options[2]}
                    defaultText="Type"
                    isOpen={openSelect === 3}
                    onToggle={() => handleSelectToggle(3)}
                />
                <SelectWithSearch
                    options={options[3]}
                    defaultText="Color"
                    isOpen={openSelect === 4}
                    onToggle={() => handleSelectToggle(4)}
                />
            </div>
        </aside>
    );
};

export default Aside;