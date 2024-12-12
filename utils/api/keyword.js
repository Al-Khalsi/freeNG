import {API_CONFIG} from '../apiConfig';

const keywordBaseUrl = API_CONFIG.KEYWORD_BASE_URL;

export const KEYWORD_API = {
    KEYWORD_URL: keywordBaseUrl,
    SEARCH: `${keywordBaseUrl}/search`,
    SEARCH_PAGINATED: (query, page, size) => `${API_CONFIG.FILE_BASE_URL}/search/keywords/paginated?query=${query}&page=${page}&size=${size}`, // search paginated list of keywords
    LIST_IMAGES_BY_KEYWORD: (keywordId, page, size) => `${API_CONFIG.FILE_BASE_URL}/keyword/${keywordId}?page=${page}&size=${size}`, // fetch paginated list of images based on keyword provided
    // KEYWORD_DETAILS_PAGINATED: (keywordId, page, size) => `${keywordBaseUrl}/list/paginated/${keywordId}?page=${page}&size=${size}`, // Fetch paginated keyword details based on ID
};