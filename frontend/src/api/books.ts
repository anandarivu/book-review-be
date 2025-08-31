import api from './axios';

// Search books (paginated, sorted, filtered)
export const getBooks = (params?: { search?: string; page?: number; size?: number }) =>
  api.get('/books', { params });

// Get book by ID
export const getBook = (id: string) => api.get(`/books/${id}`);

// Create book (BookRequest)
export const createBook = (data: {
  bookId?: string;
  title: string;
  author: string;
  description?: string;
  coverImageUrl?: string;
  genres?: string[];
  publishedYear?: number;
}) => api.post('/books', data);

// Update book (BookRequest)
export const updateBook = (id: string, data: {
  bookId?: string;
  title: string;
  author: string;
  description?: string;
  coverImageUrl?: string;
  genres?: string[];
  publishedYear?: number;
}) => api.put(`/books/${id}`, data);

// Delete book
export const deleteBook = (id: string) => api.delete(`/books/${id}`);

// Get average rating for a book
export const getBookAverageRating = (id: string) => api.get(`/books/${id}/average-rating`);

// Get top rated books
export const getTopRatedBooks = (params?: { page?: number; size?: number }) =>
  api.get('/books/top-rated', { params });
