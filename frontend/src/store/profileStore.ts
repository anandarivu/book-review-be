import { create } from 'zustand';
import { getProfile, updateProfile } from '../api/profile';
import { getFavorites, addFavorite, removeFavorite } from '../api/favorites';

interface ProfileState {
  profile: any | null;
  favorites: any[];
  loading: boolean;
  fetchProfile: () => Promise<void>;
  updateProfile: (data: any) => Promise<void>;
  fetchFavorites: () => Promise<void>;
  addFavorite: (bookId: number) => Promise<void>;
  removeFavorite: (bookId: number) => Promise<void>;
}

export const useProfileStore = create<ProfileState>((set) => ({
  profile: null,
  favorites: [],
  loading: false,
  fetchProfile: async () => {
    set({ loading: true });
    const res = await getProfile();
    set({ profile: res.data, loading: false });
  },
  updateProfile: async (data) => {
    await updateProfile(data);
    await useProfileStore.getState().fetchProfile();
  },
  fetchFavorites: async () => {
    set({ loading: true });
    const res = await getFavorites();
    set({ favorites: res.data, loading: false });
  },
  addFavorite: async (bookId) => {
    await addFavorite(bookId);
    await useProfileStore.getState().fetchFavorites();
  },
  removeFavorite: async (bookId) => {
    await removeFavorite(bookId);
    await useProfileStore.getState().fetchFavorites();
  },
}));
