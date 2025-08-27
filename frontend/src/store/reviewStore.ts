import { create } from 'zustand';
import { getReviews, createReview, updateReview, deleteReview } from '../api/reviews';

interface ReviewState {
  reviews: any[];
  loading: boolean;
  fetchReviews: (bookId: number) => Promise<void>;
  addReview: (bookId: number, data: any) => Promise<void>;
  editReview: (reviewId: number, data: any) => Promise<void>;
  removeReview: (reviewId: number, bookId: number) => Promise<void>;
}

export const useReviewStore = create<ReviewState>((set) => ({
  reviews: [],
  loading: false,
  fetchReviews: async (bookId) => {
    set({ loading: true });
    const res = await getReviews(bookId);
    set({ reviews: res.data, loading: false });
  },
  addReview: async (bookId, data) => {
    await createReview(bookId, data);
    await useReviewStore.getState().fetchReviews(bookId);
  },
  editReview: async (reviewId, data) => {
    await updateReview(reviewId, data);
    // No bookId here, so just refetch all
    // You may want to refetch for a specific book
  },
  removeReview: async (reviewId, bookId) => {
    await deleteReview(reviewId);
    await useReviewStore.getState().fetchReviews(bookId);
  },
}));
