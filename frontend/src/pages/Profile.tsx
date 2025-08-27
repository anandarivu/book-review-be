import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import { Box, CircularProgress, Typography } from '@mui/material';
import Grid from '@mui/material/Grid';
import Pagination from '@mui/material/Pagination';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getTopRatedBooks } from '../api/books';
import { getFavorites, removeFavorite, addFavorite } from '../api/favorites';
import { getReviewsForCurrentUser } from '../api/reviews';
import { getProfile } from '../api/profile';
import BookCard from '../components/BookCard';
import Navbar from '../components/Navbar';
import { handleJwtExpired } from '../utils/session';

const Profile: React.FC = () => {
  const [reviewsPage, setReviewsPage] = useState(1);
  const reviewsPerPage = 5;
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

  const handleRemoveFavorite = async (bookId: string) => {
    await removeFavorite(bookId);
    getFavorites().then(res => setFavorites(res.data));
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
              <StarBorderIcon sx={{ fontSize: 40, color: 'white', mb: 2 }} />
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
                <Box key={review.id} mb={3} p={2} bgcolor="#222" borderRadius={3} boxShadow={2}>
                  <Typography variant="subtitle1" color="error" fontWeight={700} gutterBottom>
                    {review.title}
                  </Typography>
                  <Typography variant="body2" color="white" gutterBottom>
                    {review.reviewText}
                  </Typography>
                  <Box display="flex" alignItems="center" gap={2}>
                    <Typography color="gray">Book: {review.bookTitle || review.bookId}</Typography>
                    <Typography color="gray">Rating: {review.rating}</Typography>
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
    </Box>
  );
};

export default Profile;
