import api from './axios';

// Get favorites (array of Book)
export const getFavorites = () => api.get('/profile/favorites');

// Add favorite (bookId in path)
export const addFavorite = (bookId: string) => api.post(`/profile/favorites/${bookId}`);

// Remove favorite (bookId in path)
export const removeFavorite = (bookId: string) => api.delete(`/profile/favorites/${bookId}`);
