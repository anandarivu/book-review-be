import api from './axios';

// Get current user's profile
export const getProfile = () => api.get('/profile');

// Update current user's profile (UserProfileRequest)
export const updateProfile = (data: {
  userId?: string;
  email?: string;
  // add other fields as needed
}) => api.put('/profile', data);
