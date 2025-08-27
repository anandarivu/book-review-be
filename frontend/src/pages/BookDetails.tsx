import { jwtDecode } from 'jwt-decode';
import dayjs from 'dayjs';

import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getBook } from '../api/books';
import { getFavorites, addFavorite, removeFavorite } from '../api/favorites';
import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate } from 'react-router-dom';
import { getReviewsForBook, createReview } from '../api/reviews';
import { updateReview, deleteReview } from '../api/reviews';
import { Box, Typography, Paper, TextField, Button, Divider, Rating, CircularProgress } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { useAuthStore } from '../store/authStore';
import Navbar from '../components/Navbar';

const BookDetails: React.FC = () => {
  const [isFavorite, setIsFavorite] = useState(false);
  // Pagination state for reviews
  const [reviewsPage, setReviewsPage] = useState(1);
  const REVIEWS_PER_PAGE = 5;
  // ...existing code...

  // Move hooks to top to fix hook order error
  const reviewsRef = React.useRef<HTMLDivElement>(null);
  const handleScrollToReviews = () => {
    if (reviewsRef.current) {
      reviewsRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  };

  const { token } = useAuthStore();
  const [editingReviewId, setEditingReviewId] = useState<string | null>(null);
  const [editTitle, setEditTitle] = useState('');
  const [editText, setEditText] = useState('');
  const [editRating, setEditRating] = useState<number | null>(5);
  const username = localStorage.getItem('username') || '';
  const navigate = useNavigate();
  const { id } = useParams();
  const [book, setBook] = useState<Record<string, any> | null>(null);
  const [reviews, setReviews] = useState<Array<{ id: string; title: string; reviewText: string; rating: number; userUuid: string; userId: string; createdAt?: string; updatedAt?: string }>>([]);
  const paginatedReviews = reviews.slice((reviewsPage - 1) * REVIEWS_PER_PAGE, reviewsPage * REVIEWS_PER_PAGE);
  const [reviewTitle, setReviewTitle] = useState('');
  const [reviewText, setReviewText] = useState('');
  const [rating, setRating] = useState<number | null>(0);
  const [loading, setLoading] = useState(true);

  // Set browser tab title to '<book title> by <author>' when book is loaded
  useEffect(() => {
    if (book && book.title && book.author) {
      document.title = `${book.title} by ${book.author}`;
    }
    // Optionally reset title on unmount
    return () => {
      document.title = 'Book Review';
    };
  }, [book]);

  useEffect(() => {
    if (id) {
      Promise.all([
        getBook(id),
        getReviewsForBook(id),
        getFavorites()
      ]).then(([bookRes, reviewsRes, favRes]) => {
        setBook(bookRes.data);
        setReviews(reviewsRes.data.content || reviewsRes.data);
        setIsFavorite(favRes.data.some((fav: any) => fav.id === id));
        setLoading(false);
      });
    }
  }, [id]);

  const handleReviewSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (id && rating) {
      await createReview({
        title: reviewTitle,
        reviewText,
        rating,
        bookId: id
      });
      getReviewsForBook(id).then(res => setReviews(res.data.content || res.data));
      setReviewTitle('');
      setReviewText('');
      setRating(5);
    }
  };

  if (loading || !book)
    return (
      <Box display="flex" alignItems="center" justifyContent="center" minHeight="60vh">
        <CircularProgress color="error" />
      </Box>
    );

  return (
    <Box minHeight="100vh" sx={{ backgroundColor: 'black' }}>
      <Navbar username={username} />
      <Box maxWidth="lg" mx="auto" py={10} px={4}>
        <Button startIcon={<ArrowBackIcon />} variant="outlined" color="error" sx={{ mb: 2, fontWeight: 700 }} onClick={() => navigate('/')}>Back to Home</Button>
        <Paper elevation={8} sx={{ bgcolor: '#181818', p: 4, border: '2px solid #e50914' }}>
          <Box display="flex" gap={4}>
            {book.coverImageUrl && (
              <Box minWidth={180} maxWidth={220} display="flex" alignItems="flex-start" justifyContent="center">
                <img src={book.coverImageUrl} alt={book.title} style={{ width: '100%', maxWidth: 200, borderRadius: 8, objectFit: 'cover', boxShadow: '0 2px 12px rgba(0,0,0,0.25)' }} />
              </Box>
            )}
            <Box flex={1}>
              <Box display="flex" alignItems="center" gap={2}>
                <Typography variant="h4" fontWeight={900} color="error" gutterBottom>
                  {book.title}
                </Typography>
                <IconButton
                  color={isFavorite ? 'error' : 'default'}
                  onClick={async () => {
                    if (isFavorite) {
                      await removeFavorite(book.id);
                    } else {
                      await addFavorite(book.id);
                    }
                    const favRes = await getFavorites();
                    setIsFavorite(favRes.data.some((fav: any) => fav.id === book.id));
                  }}
                  sx={{ ml: 1 }}
                >
                  {isFavorite ? <FavoriteIcon /> : <FavoriteBorderIcon />}
                </IconButton>
              </Box>
              {typeof book.averageRating === 'number' && (
                <Box display="flex" alignItems="center" gap={1}>
                  <Rating value={book.averageRating} precision={0.1} readOnly size="small" sx={{ color: '#e50914' }} />
                  <Typography color="white" fontWeight={700}>
                    {book.averageRating.toFixed(1)}
                  </Typography>
                  <Typography color="gray" fontSize={14}>
                    <span
                      style={{ cursor: 'pointer', textDecoration: 'underline' }}
                      onClick={handleScrollToReviews}
                      title="Scroll to reviews"
                    >
                      ({reviews.length} reviews)
                    </span>
                  </Typography>
                </Box>
              )}
              <Typography variant="subtitle1" color="white" gutterBottom>
                by {book.author}
              </Typography>
              <Typography variant="body1" color="gray" gutterBottom>
                {book.description}
              </Typography>
            </Box>
          </Box>
          <Divider sx={{ my: 3, bgcolor: '#e50914' }} />
          <Typography variant="h5" fontWeight={700} color="error" gutterBottom>
            Reviews
          </Typography>
          <Box mb={3} ref={reviewsRef}>
            {reviews.length === 0 ? (
              <Typography color="gray">No reviews yet.</Typography>
            ) : (
              paginatedReviews.map((review) => {
                let isMine = false;
                try {
                  if (token && review.userUuid) {
                    const decoded: any = jwtDecode(token);
                    const userUuid = decoded?.uuid;
                    isMine = review.userUuid === userUuid;
                  }
                } catch {}
                return (
                  <Paper key={review.id} sx={{ bgcolor: '#222', p: 2, mb: 2 }}>
                    {editingReviewId === review.id ? (
                      <form
                        onSubmit={async e => {
                          e.preventDefault();
                          await updateReview(review.id, {
                            title: editTitle,
                            reviewText: editText,
                            rating: editRating || 5,
                            bookId: id || ''
                          });
                          getReviewsForBook(id || '').then(res => setReviews(res.data.content || res.data));
                          setEditingReviewId(null);
                        }}
                      >
                        <TextField
                          label="Review Title"
                          variant="filled"
                          value={editTitle}
                          onChange={e => setEditTitle(e.target.value)}
                          fullWidth
                          InputProps={{ style: { backgroundColor: 'black', color: 'white' } }}
                          InputLabelProps={{ style: { color: '#e50914' } }}
                          sx={{ mb: 2 }}
                        />
                        <TextField
                          label="Review Text"
                          variant="filled"
                          multiline
                          minRows={3}
                          value={editText}
                          onChange={e => setEditText(e.target.value)}
                          fullWidth
                          InputProps={{ style: { backgroundColor: 'black', color: 'white' } }}
                          InputLabelProps={{ style: { color: '#e50914' } }}
                          sx={{ mb: 2 }}
                        />
                        <Box display="flex" alignItems="center" mb={2}>
                          <Typography color="white" sx={{ mr: 2 }}>Rating:</Typography>
                          <Rating
                            value={editRating}
                            onChange={(_, value) => setEditRating(value)}
                            max={5}
                            sx={{ color: '#e50914' }}
                          />
                        </Box>
                        <Box display="flex" justifyContent="flex-end" gap={2} mt={2}>
                          <Button variant="outlined" sx={{ fontWeight: 700, color: 'white', borderColor: 'white', bgcolor: 'transparent', '&:hover': { borderColor: '#e50914', color: '#e50914', bgcolor: 'transparent' } }} onClick={() => setEditingReviewId(null)}>
                            Cancel
                          </Button>
                          <Button type="submit" variant="contained" color="error" sx={{ fontWeight: 700 }}>
                            Save
                          </Button>
                        </Box>
                      </form>
                    ) : (
                      <>
                        <Box display="flex" alignItems="center" justifyContent="space-between" mb={1}>
                          <Typography variant="caption" color="gray">
                            {review.userId}
                          </Typography>
                          <Typography variant="caption" color="gray" sx={{ ml: 2 }}>
                            {review.updatedAt ? dayjs(review.updatedAt).format('MMM D, YYYY h:mm A') : ''}
                          </Typography>
                          {isMine && (
                            <Box display="flex" gap={1}>
                              <IconButton size="small" color="error" onClick={() => {
                                setEditingReviewId(review.id);
                                setEditTitle(review.title);
                                setEditText(review.reviewText);
                                setEditRating(review.rating);
                              }}>
                                <EditIcon fontSize="small" />
                              </IconButton>
                              <IconButton size="small" color="error" onClick={async () => {
                                await deleteReview(review.id);
                                getReviewsForBook(id || '').then(res => setReviews(res.data.content || res.data));
                              }}>
                                <DeleteIcon fontSize="small" />
                              </IconButton>
                            </Box>
                          )}
                        </Box>
                        <Box display="flex" alignItems="center" mb={1}>
                          <Rating value={review.rating} readOnly max={5} sx={{ color: '#e50914', mr: 1 }} />
                        </Box>
                        <Typography variant="subtitle2" color="error" fontWeight={700}>
                          {review.title}
                        </Typography>
                        <Typography variant="body2" color="white">
                          {review.reviewText}
                        </Typography>
                      </>
                    )}
                  </Paper>
                );
              })
            )}
            {/* Pagination controls */}
            {reviews.length > REVIEWS_PER_PAGE && (
              <Box display="flex" justifyContent="center" mt={2}>
                <Button
                  variant="outlined"
                  color="error"
                  disabled={reviewsPage === 1}
                  onClick={() => setReviewsPage(reviewsPage - 1)}
                  sx={{ mx: 1 }}
                >
                  Previous
                </Button>
                <Typography color="white" sx={{ mx: 2, mt: 1 }}>
                  Page {reviewsPage} of {Math.ceil(reviews.length / REVIEWS_PER_PAGE)}
                </Typography>
                <Button
                  variant="outlined"
                  color="error"
                  disabled={reviewsPage === Math.ceil(reviews.length / REVIEWS_PER_PAGE)}
                  onClick={() => setReviewsPage(reviewsPage + 1)}
                  sx={{ mx: 1 }}
                >
                  Next
                </Button>
              </Box>
            )}
          </Box>
          <form onSubmit={handleReviewSubmit}>
            <Typography variant="h6" color="error" fontWeight={700} gutterBottom>
              Add a Review
            </Typography>
            <Box display="flex" alignItems="center" mb={2}>
              <Typography color="white" sx={{ mr: 2 }}>Rating:</Typography>
              <Rating
                value={rating}
                onChange={(_, value) => setRating(value)}
                max={5}
                sx={{ color: '#e50914' }}
              />
            </Box>
            <TextField
              label="Review Title"
              variant="filled"
              value={reviewTitle}
              onChange={e => setReviewTitle(e.target.value)}
              fullWidth
              InputProps={{ style: { backgroundColor: 'black', color: 'white' } }}
              InputLabelProps={{ style: { color: '#e50914' } }}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Review Text"
              variant="filled"
              multiline
              minRows={3}
              value={reviewText}
              onChange={e => setReviewText(e.target.value)}
              fullWidth
              InputProps={{ style: { backgroundColor: 'black', color: 'white' } }}
              InputLabelProps={{ style: { color: '#e50914' } }}
              sx={{ mb: 2 }}
            />
            <Box display="flex" justifyContent="flex-end" mt={2}>
              <Button type="submit" variant="contained" color="error" sx={{ fontWeight: 700 }}>
                Submit Review
              </Button>
            </Box>
          </form>
        </Paper>
      </Box>
    </Box>
  );
};

export default BookDetails;
