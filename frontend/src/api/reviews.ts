import api from './axios';

// Get reviews for a book (paginated)
export const getReviewsForBook = (bookId: string, params?: { arg1?: number; arg2?: number; arg3?: string }) =>
  api.get(`/reviews/book/${bookId}`, { params });

// Get reviews for current user (paginated)
export const getReviewsForCurrentUser = (params?: { arg1?: number; arg2?: number; arg3?: string }) =>
  api.get('/reviews/user/me', { params });

// Get review by ID
export const getReview = (id: string) => api.get(`/reviews/${id}`);

// Create review (ReviewRequest)
export const createReview = (data: {
  title: string;
  reviewText: string;
  rating: number;
  bookId: string;
}) => api.post('/reviews', data);

// Update review (ReviewRequest)
export const updateReview = (reviewId: string, data: {
  title: string;
  reviewText: string;
  rating: number;
  bookId: string;
}) => api.put(`/reviews/${reviewId}`, data);

// Delete review
export const deleteReview = (reviewId: string) => api.delete(`/reviews/${reviewId}`);

// Get average rating for a book
export const getBookAverageRating = (bookId: string) => api.get(`/reviews/book/${bookId}/average-rating`);
