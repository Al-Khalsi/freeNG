import {API_CONFIG} from '../apiConfig';

const fileBaseUrl = API_CONFIG.FILE_BASE_URL;

export const FILE_API = {
    FILE_BASE_URL: fileBaseUrl,
    LIST_IMAGES: `${fileBaseUrl}/list`,
    LIST_IMAGES_PAGINATED: (page, size) => `${fileBaseUrl}/list/paginated?page=${page}&size=${size}`,
    UPLOAD: `${fileBaseUrl}/upload`,
    SEARCH_PAGINATED: (page, size, query) => `${fileBaseUrl}/search/paginated?page=${page}&size=${size}&query=${query}`,
    DOWNLOAD: (fileId) => `${fileBaseUrl}/download/${fileId}`,
    UPDATE: (fileId) => `${fileBaseUrl}/${fileId}`,
    DELETE: (fileId) => `${fileBaseUrl}/${fileId}`,
};