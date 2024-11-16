import React, { useState, useEffect } from 'react';
import axios from 'axios';

const AddCategoryModal = ({ isOpen, onClose, onCategoryAdded }) => {
    const [isSubCategory, setIsSubCategory] = useState(false);
    const [categoryName, setCategoryName] = useState('');
    const [subCategoryName, setSubCategoryName] = useState('');
    const [parentCategories, setParentCategories] = useState([]);
    const [selectedParentId, setSelectedParentId] = useState('');

    useEffect(() => {
        const fetchParentCategories = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/v1/category/list/parent');
                setParentCategories(response.data.data);
            } catch (error) {
                console.error('Error fetching parent categories:', error);
            }
        };

        if (isOpen) {
            fetchParentCategories();
        }
    }, [isOpen]);

    const handleAddCategory = async () => {
        try {
            const categoryData = {
                name: categoryName,
                description: '',
                iconUrl: '',
                displayOrder: 0,
                level: 0,
                parent: true,
                active: true,
            };

            await axios.post('http://localhost:8080/api/v1/category', categoryData);
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
                description: '',
                iconUrl: '',
                displayOrder: 0,
                level: 1,
                parentId: selectedParentId,
                active: true,
            };

            await axios.post('http://localhost:8080/api/v1/category', subCategoryData);
            onCategoryAdded();
            onClose();
        } catch (error) {
            console.error('Error adding subcategory:', error);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="modal">
            <div className="modal-content">
                <span className="close" onClick={onClose}>&times;</span>
                <h2>Add Category/Subcategory</h2>
                <div>
                    <label>
                        <input
                            type="radio"
                            checked={!isSubCategory}
                            onChange={() => setIsSubCategory(false)}
                        />
                        Category
                    </label>
                    <label>
                        <input
                            type="radio"
                            checked={isSubCategory}
                            onChange={() => setIsSubCategory(true)}
                        />
                        Subcategory
                    </label>
                </div>

                {!isSubCategory ? (
                    <>
                        <input
                            type="text"
                            placeholder="Category Name"
                            value={categoryName}
                            onChange={(e) => setCategoryName(e.target.value)}
                        />
                        <button onClick={handleAddCategory}>Add Category</button>
                    </>
                ) : (
                    <>
                        <select onChange={(e) => setSelectedParentId(e.target.value)} required>
                            <option value="">Select Parent Category</option>
                            {parentCategories.map((cat) => (
                                <option key={cat.id} value={cat.id}>
                                    {cat.name}
                                </option>
                            ))}
                        </select>
                        <input
                            type="text"
                            placeholder="Subcategory Name"
                            value={subCategoryName}
                            onChange={(e) => setSubCategoryName(e.target.value)}
                        />
                        <button onClick={handleAddSubCategory}>Add Subcategory</button>
                    </>
                )}
            </div>
        </div>
    );
};

export default AddCategoryModal;