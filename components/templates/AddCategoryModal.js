import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../../context/AuthContext';

const AddCategoryModal = ({ isOpen, onClose, onCategoryAdded }) => {
    const { token } = useAuth();
    const [isSubCategory, setIsSubCategory] = useState(false);
    const [categoryName, setCategoryName] = useState('');
    const [subCategoryName, setSubCategoryName] = useState('');
    const [parentCategories, setParentCategories] = useState([]);
    const [selectedParentId, setSelectedParentId] = useState('');

    useEffect(() => {
        const fetchParentCategories = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/v1/category/list/parent', {
                    headers: {
                        Authorization: `Bearer ${token}`, // Add the token to the headers
                    },
                });

                setParentCategories(response.data.data);
            } catch (error) {
                console.error('Error fetching parent categories:', error);
            }
        };

        if (isOpen) {
            fetchParentCategories();
        }
    }, [isOpen, token]); // Ensure token is included in the dependency array

    const handleAddCategory = async () => {
        try {
            const categoryData = {
                name: categoryName,
                // description: '',
                // iconUrl: '',
                // displayOrder: 0,
                // level: 0,
                // parent: true,
                active: true,
            };

            await axios.post('http://localhost:8080/api/v1/category', categoryData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            onCategoryAdded();
            onClose();
        } catch (error) {
            console.error('Error adding category:', error);
        }
    };

    const handleAddSubCategory = async () => {
        try {
            const subCategoryData = {
                name: subCategoryName,
                // description: '',
                // iconUrl: '',
                // displayOrder: 0,
                // level: 1,
                parentId: selectedParentId,
                active: true,
            };

            await axios.post('http://localhost:8080/api/v1/category', subCategoryData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            onCategoryAdded();
            onClose();
        } catch (error) {
            console.error('Error adding subcategory:', error);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="modal fixed top-0 right-0 bottom-0 left-0 flex justify-center items-center bg-white/[.3]">
            <div className="modal-content relative w-80 p-5 rounded bg-bgDarkGray">
                <span className="close absolute right-0 top-0 w-4 h-4 flex justify-center items-center p-3 bg-red-600 rounded-full cursor-pointer " onClick={onClose}>&times;</span>
                <h3 className='text-black'>Add Filter</h3>

                <div class="radio-input flex items-center gap-1 p-1 rounded-lg -z-10 bg-bgBlack">
                    <label class="label flex flex-col justify-center items-center">
                        <input className='hidden' value="value-1" checked={!isSubCategory} onChange={() => setIsSubCategory(false)} name="value-radio" id="value-1" type="radio" />
                        <span class="text">Category</span>
                    </label>
                    <label class="label">
                        <input className='hidden' value="value-1" checked={isSubCategory} onChange={() => setIsSubCategory(true)} name="value-radio" id="value-1" type="radio" />
                        <span class="text">SubCategorhy</span>
                    </label>
                </div>

                {!isSubCategory ? (
                    <>
                        <input
                            type="text"
                            className='text-black'
                            placeholder="Category Name"
                            value={categoryName}
                            onChange={(e) => setCategoryName(e.target.value)}
                        />
                        <button onClick={handleAddCategory} className='bg-green-600 text-white'>Add Category</button>
                    </>
                ) : (
                    <>
                        <select onChange={(e) => setSelectedParentId(e.target.value)} required className='text-black bg-bgGray'>
                            <option value="">Select Parent Category</option>
                            {parentCategories.map((cat) => (
                                <option key={cat.id} value={cat.id}>
                                    {cat.name}
                                </option>
                            ))}
                        </select>
                        <input
                            type="text"
                            className='text-black'
                            placeholder="Subcategory Name"
                            value={subCategoryName}
                            onChange={(e) => setSubCategoryName(e.target.value)}
                        />
                        <button onClick={handleAddSubCategory} className='text-black'>Add Subcategory</button>
                    </>
                )}
            </div>
        </div>
    );
};

export default AddCategoryModal;