import React from 'react';
import { Box, Typography, Grid, Pagination, FormControl, Select, MenuItem, InputLabel, CircularProgress } from '@mui/material';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import BookCard from '../components/BookCard';
import { getSimilarBooksRecommendations, getLlmRecommendations } from '../api/recommendations';
import { getTopRatedBooks } from '../api/books';

const RecommendationsSection: React.FC<{
  recommendations: any[];
  favorites: any[];
  itemsPerPage: number;
  recPage: number;
  setRecPage: (page: number) => void;
  handleAddFavorite: (bookId: string) => void;
  handleRemoveFavorite: (bookId: string) => void;
}> = ({ recommendations, favorites, itemsPerPage, recPage, setRecPage, handleAddFavorite, handleRemoveFavorite }) => {
  const [recommendationType, setRecommendationType] = React.useState('topRated');
  const [similarBooks, setSimilarBooks] = React.useState<any[]>([]);
  const [similarTotal, setSimilarTotal] = React.useState(0);
  const [topRatedBooks, setTopRatedBooks] = React.useState<any[]>([]);
  const [topRatedTotal, setTopRatedTotal] = React.useState(0);
  const [llmBooks, setLlmBooks] = React.useState<any[]>([]);
  const [llmTotal, setLlmTotal] = React.useState(0);
  const [loading, setLoading] = React.useState(false);

  React.useEffect(() => {
    if (recommendationType === 'similar') {
      setLoading(true);
      getSimilarBooksRecommendations({ page: recPage - 1, size: itemsPerPage })
        .then(res => {
          const data = res.data.content || res.data;
          setSimilarBooks(data);
          setSimilarTotal(res.data.totalElements || data.length);
        })
        .finally(() => setLoading(false));
    } else if (recommendationType === 'topRated') {
      setLoading(true);
      getTopRatedBooks({ page: recPage - 1, size: itemsPerPage })
        .then(res => {
          const data = res.data.content || res.data;
          setTopRatedBooks(data);
          setTopRatedTotal(res.data.totalElements || data.length);
        })
        .finally(() => setLoading(false));
    } else if (recommendationType === 'llm' && llmBooks.length === 0) {
      setLoading(true);
      getLlmRecommendations()
        .then(res => {
          const data = res.data.content || res.data;
          setLlmBooks(data);
          setLlmTotal(data.length);
        })
        .finally(() => setLoading(false));
    }
  }, [recommendationType, recPage, itemsPerPage]);

  // Rendering logic
  const booksToShow = recommendationType === 'llm'
    ? llmBooks.slice((recPage - 1) * itemsPerPage, recPage * itemsPerPage)
    : recommendationType === 'similar'
      ? similarBooks
      : recommendationType === 'topRated'
        ? topRatedBooks
        : recommendations;
  const totalBooks = recommendationType === 'llm'
    ? llmTotal
    : recommendationType === 'similar'
      ? similarTotal
      : recommendationType === 'topRated'
        ? topRatedTotal
        : recommendations.length;

  return (
    <>
      <Box mt={8} mb={4} display="flex" alignItems="center" justifyContent="space-between">
        <Typography variant="h4" fontWeight={900} color="error" align="left" gutterBottom>
          Recommendations
        </Typography>
        <FormControl size="small" sx={{ minWidth: 180 }}>
          <InputLabel id="recommendation-type-label">Type</InputLabel>
          <Select
            labelId="recommendation-type-label"
            value={recommendationType}
            label="Type"
            onChange={e => setRecommendationType(e.target.value)}
          >
            <MenuItem value="topRated">Top Rated</MenuItem>
            <MenuItem value="similar">Similar Books</MenuItem>
            <MenuItem value="llm">LLM Based</MenuItem>
          </Select>
        </FormControl>
      </Box>
      {loading ? (
        <Box display="flex" alignItems="center" justifyContent="center" minHeight="200px">
          <CircularProgress color="error" />
        </Box>
      ) : booksToShow.length === 0 ? (
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
            {booksToShow.map((book: any) => (
              <Box key={book.id} sx={{ width: { xs: '100%', sm: '50%', md: '33.33%', lg: '25%' }, p: 1 }}>
                <BookCard
                  book={book}
                  isFavorite={favorites.some((fav: any) => fav.id === book.id)}
                  onFavorite={() => {
                    if (favorites.some((fav: any) => fav.id === book.id)) {
                      handleRemoveFavorite(book.id);
                    } else {
                      handleAddFavorite(book.id);
                    }
                  }}
                  showFavorite={true}
                  showViewDetails={true}
                />
              </Box>
            ))}
          </Grid>
          {totalBooks > itemsPerPage && (
            <Box display="flex" justifyContent="center" mt={3}>
              <Pagination
                count={Math.ceil(totalBooks / itemsPerPage)}
                page={recPage}
                onChange={(_event: React.ChangeEvent<unknown>, value: number) => setRecPage(value)}
                color="primary"
              />
            </Box>
          )}
        </>
      )}
    </>
  );
};

export default RecommendationsSection;
