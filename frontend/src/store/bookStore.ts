import { create } from 'zustand';
import { getBooks, getBook, createBook, updateBook, deleteBook } from '../api/books';

interface BookState {
  books: any[];
  selectedBook: any | null;
  loading: boolean;
  fetchBooks: () => Promise<void>;
  fetchBook: (id: number) => Promise<void>;
  addBook: (data: any) => Promise<void>;
  editBook: (id: number, data: any) => Promise<void>;
  removeBook: (id: number) => Promise<void>;
}

export const useBookStore = create<BookState>((set) => ({
  books: [],
  selectedBook: null,
  loading: false,
  fetchBooks: async () => {
    set({ loading: true });
    const res = await getBooks();
    set({ books: res.data.content || res.data, loading: false });
  },
  fetchBook: async (id) => {
    set({ loading: true });
    const res = await getBook(id);
    set({ selectedBook: res.data, loading: false });
  },
  addBook: async (data) => {
    await createBook(data);
    await useBookStore.getState().fetchBooks();
  },
  editBook: async (id, data) => {
    await updateBook(id, data);
    await useBookStore.getState().fetchBooks();
  },
  removeBook: async (id) => {
    await deleteBook(id);
    await useBookStore.getState().fetchBooks();
  },
}));
