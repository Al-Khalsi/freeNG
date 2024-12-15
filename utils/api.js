import axios from 'axios';

export const apiFetch = async (url, method = 'GET', body = null, customOptions = {}) => {
    const options = {
        method,
        ...customOptions, // Add custom options
    };

    try {
        const response = await axios({
            url,
            method,
            data: body instanceof FormData ? body : body ? JSON.stringify(body) : null,
            ...options, // Include any other options passed in
        });

        // Axios automatically resolves the response, so we can return response.data directly
        return response.data;
    } catch (error) {
        throw error; // Re-throw the error for handling in the calling function
    }
};