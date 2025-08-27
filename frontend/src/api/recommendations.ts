import api from './axios';

// ...existing code...

export const getSimilarBooksRecommendations = (params?: { page?: number; size?: number }) =>
  api.get('/books/recommendations', { params });

// ...existing code...
