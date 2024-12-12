const API_VERSION = process.env.NEXT_PUBLIC_BACKEND_API_VERSION; // e.g., v1, v2

const SERVER_URL = process.env.NODE_ENV === 'production'
    ? process.env.NEXT_PUBLIC_BACKEND_BASE_URL_PRODUCTION // e.g., https://api.v1.pixelfreebies.com/api
    : process.env.NEXT_PUBLIC_BACKEND_BASE_URL_LOCAL; // e.g., http://server:9090/api

const BASE_URL = `${SERVER_URL}/${API_VERSION}`; // e.g., https://api.v1.pixelfreebies.com/api/v1 || http://server:9090/api/v1

export const API_CONFIG = {
    BASE_URL,
    API_VERSION,
    AUTH_BASE_URL: `${BASE_URL}/auth`,
    FILE_BASE_URL: `${BASE_URL}/file`,
    KEYWORD_BASE_URL: `${BASE_URL}/keywords`,
};