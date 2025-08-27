import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import NoteAddIcon from '@mui/icons-material/NoteAdd';
import { Box, CircularProgress, Typography } from '@mui/material';
import Grid from '@mui/material/Grid';
import Pagination from '@mui/material/Pagination';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getTopRatedBooks } from '../api/books';
import { getFavorites, removeFavorite, addFavorite } from '../api/favorites';
import { getReviewsForCurrentUser, updateReview, deleteReview } from '../api/reviews';
import { getProfile } from '../api/profile';
import BookCard from '../components/BookCard';
import Navbar from '../components/Navbar';
import { handleJwtExpired } from '../utils/session';
import Rating from '@mui/material/Rating';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import IconButton from '@mui/material/IconButton';
import dayjs from 'dayjs';

const Profile: React.FC = () => {
  const [reviewsPage, setReviewsPage] = useState(1);
  const reviewsPerPage = 2;
  const [userReviews, setUserReviews] = useState<any[]>([]);
  // Add favorite for recommendations
  const handleAddFavorite = async (bookId: string) => {
    await addFavorite(bookId);
    getFavorites().then(res => setFavorites(res.data));
  };
  const navigate = useNavigate();
  const [profile, setProfile] = useState<any>(null);
  const [favorites, setFavorites] = useState<any[]>([]);
  const [recommendations, setRecommendations] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [favPage, setFavPage] = useState(1);
  const [recPage, setRecPage] = useState(1);
  const itemsPerPage = 8;

  const [editReview, setEditReview] = useState<any>(null);
  const [editOpen, setEditOpen] = useState(false);
  const [editTitle, setEditTitle] = useState('');
  const [editText, setEditText] = useState('');
  const [editRating, setEditRating] = useState(0);

  useEffect(() => {
    Promise.all([
      getProfile().catch(err => {
        if (err?.response?.status === 401) {
          handleJwtExpired(navigate);
        }
        throw err;
      }),
      getFavorites().catch(err => {
        if (err?.response?.status === 401) {
          handleJwtExpired(navigate);
        }
        throw err;
      }),
      getTopRatedBooks({ size: 20 }).catch(err => {
        if (err?.response?.status === 401) {
          handleJwtExpired(navigate);
        }
        throw err;
      }),
      getReviewsForCurrentUser().catch(err => {
        if (err?.response?.status === 401) {
          handleJwtExpired(navigate);
        }
        throw err;
      })
    ])
      .then(([profileRes, favoritesRes, topRatedRes, userReviewsRes]) => {
        setProfile(profileRes.data);
        const recs = topRatedRes.data.content || topRatedRes.data;
        setRecommendations(recs);
        // Add reviewCount and averageRating to favorites from recommendations if available
        const favsWithRatings = favoritesRes.data.map((fav: any) => {
          const rec = recs.find((r: any) => r.id === fav.id);
          return rec
            ? { ...fav, reviewCount: rec.reviewCount, averageRating: rec.averageRating }
            : fav;
        });
        setFavorites(favsWithRatings);
        setUserReviews(userReviewsRes.data.content || userReviewsRes.data);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
      });
  }, []);

  const refreshUserReviews = async () => {
    const res = await getReviewsForCurrentUser();
    setUserReviews(res.data.content || res.data);
  };

  const handleRemoveFavorite = async (bookId: string) => {
    await removeFavorite(bookId);
    getFavorites().then(res => setFavorites(res.data));
  };

  const handleEditClick = (review: any) => {
    setEditReview(review);
    setEditTitle(review.title);
    setEditText(review.reviewText);
    setEditRating(review.rating);
    setEditOpen(true);
  };

  const handleEditSave = async () => {
    if (!editReview) return;
    await updateReview(editReview.id, {
      title: editTitle,
      reviewText: editText,
      rating: editRating,
      bookId: editReview.bookId,
    });
    setEditOpen(false);
    setEditReview(null);
    await refreshUserReviews();
  };

  const handleDeleteReview = async (reviewId: string) => {
    await deleteReview(reviewId);
    await refreshUserReviews();
  };

  if (loading || !profile)
    return (
      <Box minHeight="100vh" sx={{ backgroundColor: 'black', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <CircularProgress color="error" />
      </Box>
    );

  return (
    <Box minHeight="100vh" sx={{ backgroundColor: 'black' }}>
      <Navbar username={profile.username} />
      <Box maxWidth="lg" mx="auto" py={10} px={4}>
        {/* My Reviews Section */}
        <Box mb={8}>
          <Typography variant="h4" fontWeight={900} color="error" align="left" gutterBottom>
            My Reviews
          </Typography>
          {userReviews.length === 0 ? (
            <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" minHeight="200px" border="2px dashed #e50914" borderRadius={4} bgcolor="#181818" sx={{ boxShadow: 8 }}>
              <NoteAddIcon sx={{ fontSize: 40, color: 'white', mb: 2 }} />
              <Typography variant="h6" color="error" fontWeight={700} gutterBottom>
                No reviews yet
              </Typography>
              <Typography variant="body2" color="gray" align="center">
                You haven't written any reviews yet.
              </Typography>
            </Box>
          ) : (
            <Box>
              {userReviews.slice((reviewsPage - 1) * reviewsPerPage, reviewsPage * reviewsPerPage).map((review: any) => (
                <Box key={review.id} mb={3} p={2} bgcolor="#222" borderRadius={3} boxShadow={2} display="flex" alignItems="flex-start" gap={3}>
                  {/* Book Cover */}
                  <Box minWidth={100} maxWidth={120} height={160} display="flex" alignItems="center" justifyContent="center" bgcolor="#181818" borderRadius={2} overflow="hidden">
                    <img
                      src={review.book?.coverImageUrl || ''}
                      alt={review.book?.title || 'Book cover'}
                      style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '8px' }}
                    />
                  </Box>
                  {/* Review Details */}
                  <Box flex={1} position="relative">
                    <Box position="absolute" top={0} right={0} display="flex" gap={1}>
                      <IconButton onClick={() => handleEditClick(review)} size="small">
                        <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24" fill="#e50914"><path d="M0 0h24v24H0V0z" fill="none" /><path d="M3 17.25V21h3.75l11.06-11.06-3.75-3.75L3 17.25zm14.71-9.04c.39-.39.39-1.02 0-1.41l-2.54-2.54a.9959.9959 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" /></svg>
                      </IconButton>
                      <IconButton onClick={() => handleDeleteReview(review.id)} size="small">
                        <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24" fill="#e50914"><path d="M0 0h24v24H0V0z" fill="none" /><path d="M16 9v10H8V9h8m-1.5-6h-5l-1 1H5v2h14V4h-4.5l-1-1z" /></svg>
                      </IconButton>
                    </Box>
                    <Typography variant="h6" color="error" fontWeight={900} gutterBottom>
                      {review.book?.title} <span style={{ color: '#bbb', fontWeight: 400 }}>
                        by {review.book?.author}
                      </span>
                    </Typography>
                    <Box display="flex" alignItems="center" gap={1} mb={1}>
                      <Typography variant="body2" color="warning.main" fontWeight={700}>
                        Rating:
                      </Typography>
                      <Box>
                        <span style={{ verticalAlign: 'middle' }}>
                          <Rating value={review.rating} readOnly max={5} precision={0.5} size="small" />
                        </span>
                      </Box>
                    </Box>
                    <Typography variant="subtitle1" color="error" fontWeight={700} gutterBottom>
                      {review.title}
                    </Typography>
                    <Typography variant="body2" color="white">
                      {review.reviewText}
                    </Typography>
                    <Typography variant="caption" color="gray" gutterBottom>
                      {review.updatedAt ? dayjs(review.updatedAt).format('MMM D, YYYY h:mm A') : ''}
                    </Typography>
                  </Box>
                </Box>
              ))}
              {userReviews.length > reviewsPerPage && (
                <Box display="flex" justifyContent="center" mt={2}>
                  <Pagination
                    count={Math.ceil(userReviews.length / reviewsPerPage)}
                    page={reviewsPage}
                    onChange={(_event: React.ChangeEvent<unknown>, value: number) => setReviewsPage(value)}
                    color="primary"
                  />
                </Box>
              )}
            </Box>
          )}
        </Box>
        {/* Favorites Section */}
        <Box display="flex" alignItems="center" mb={4}>
          <Typography variant="h4" fontWeight={900} color="error" align="left" gutterBottom>
            Favorites
          </Typography>
        </Box>
        {favorites.length === 0 ? (
          <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" minHeight="300px" border="2px dashed #e50914" borderRadius={4} bgcolor="#181818" sx={{ boxShadow: 8 }}>
            <FavoriteBorderIcon sx={{ fontSize: 60, color: 'white', mb: 2 }} />
            <Typography variant="h6" color="error" fontWeight={700} gutterBottom>
              No favorites yet
            </Typography>
            <Typography variant="body2" color="gray" align="center">
              Your favorite books will appear here. Start exploring and add some!
            </Typography>
          </Box>
        ) : (
          <>
            <Grid container spacing={4}>
              {favorites.slice((favPage - 1) * itemsPerPage, favPage * itemsPerPage).map(book => (
                <Grid item key={book.id} xs={12} sm={6} md={4} lg={3}>
                  <BookCard
                    book={book}
                    isFavorite={true}
                    onFavorite={() => handleRemoveFavorite(book.id)}
                    showFavorite={true}
                    showViewDetails={true}
                  />
                </Grid>
              ))}
            </Grid>
            {favorites.length > itemsPerPage && (
              <Box display="flex" justifyContent="center" mt={3}>
                <Pagination
                  count={Math.ceil(favorites.length / itemsPerPage)}
                  page={favPage}
                  onChange={(_event: React.ChangeEvent<unknown>, value: number) => setFavPage(value)}
                  color="primary"
                />
              </Box>
            )}
          </>
        )}
        {/* Recommendations Section */}
        <Box mt={8} mb={4}>
          <Typography variant="h4" fontWeight={900} color="error" align="left" gutterBottom>
            Recommendations
          </Typography>
        </Box>
        {recommendations.length === 0 ? (
          <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" minHeight="300px" border="2px dashed #e50914" borderRadius={4} bgcolor="#181818" sx={{ boxShadow: 8 }}>
            <StarBorderIcon sx={{ fontSize: 60, color: 'white', mb: 2 }} />
            <Typography variant="h6" color="error" fontWeight={700} gutterBottom>
              No recommendations yet
            </Typography>
            <Typography variant="body2" color="gray" align="center">
              Book recommendations will appear here based on your activity.
            </Typography>
          </Box>
        ) : (
          <>
            <Grid container spacing={4}>
              {recommendations.slice((recPage - 1) * itemsPerPage, recPage * itemsPerPage).map(book => (
                <Grid item key={book.id} xs={12} sm={6} md={4} lg={3}>
                  <BookCard
                    book={book}
                    isFavorite={favorites.some(fav => fav.id === book.id)}
                    onFavorite={() => {
                      if (favorites.some(fav => fav.id === book.id)) {
                        handleRemoveFavorite(book.id);
                      } else {
                        handleAddFavorite(book.id);
                      }
                    }}
                    showFavorite={true}
                    showViewDetails={true}
                  />
                </Grid>
              ))}
            </Grid>
            {recommendations.length > itemsPerPage && (
              <Box display="flex" justifyContent="center" mt={3}>
                <Pagination
                  count={Math.ceil(recommendations.length / itemsPerPage)}
                  page={recPage}
                  onChange={(_event: React.ChangeEvent<unknown>, value: number) => setRecPage(value)}
                  color="primary"
                />
              </Box>
            )}
          </>
        )}
      </Box>
      {/* Edit Review Dialog */}
      <Dialog open={editOpen} onClose={() => setEditOpen(false)}>
        <DialogTitle>Edit Review</DialogTitle>
        <DialogContent>
          {editReview && (
            <Box mb={2} display="flex" alignItems="center" gap={2}>
              <img src={editReview.book?.coverImageUrl || ''} alt={editReview.book?.title || 'Book cover'} style={{ width: 60, height: 80, objectFit: 'cover', borderRadius: 6 }} />
              <Box>
                <Typography variant="subtitle2" color="error" fontWeight={700}>{editReview.book?.title}</Typography>
                <Typography variant="body2" color="gray">by {editReview.book?.author}</Typography>
              </Box>
            </Box>
          )}
          <Box mb={2} display="flex" alignItems="center" gap={2}>
            <Typography variant="body2" color="textSecondary">Rating</Typography>
            <Rating
              value={editRating}
              onChange={(_, value) => setEditRating(value || 0)}
              max={5}
              precision={0.5}
              size="large"
            />
          </Box>
          <TextField
            label="Review Title"
            value={editTitle}
            onChange={e => setEditTitle(e.target.value)}
            fullWidth
            margin="normal"
          />
          <TextField
            label="Review Text"
            value={editText}
            onChange={e => setEditText(e.target.value)}
            fullWidth
            margin="normal"
            multiline
            rows={4}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditOpen(false)} color="secondary">Cancel</Button>
          <Button onClick={handleEditSave} color="primary" variant="contained">Save</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Profile;
