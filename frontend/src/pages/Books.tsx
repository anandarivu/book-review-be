import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { getBooks } from '../api/books';
import { Box, Typography, CircularProgress, IconButton } from '@mui/material';
import BookCard from '../components/BookCard';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import ClearIcon from '@mui/icons-material/Clear';
import SearchIcon from '@mui/icons-material/Search';
import Grid from '@mui/material/Grid';
// ...existing code...
import { addFavorite, removeFavorite, getFavorites } from '../api/favorites';
import Pagination from '@mui/material/Pagination';

const Books: React.FC = () => {
  const navigate = useNavigate();
  const [search, setSearch] = useState('');
  const [books, setBooks] = useState<Array<{ id: string; title: string; author: string; genres: string[]; description: string; coverImageUrl?: string; publishedYear?: number; averageRating?: number }>>([]);
  const [loading, setLoading] = useState(true);
  const [favorites, setFavorites] = useState<string[]>([]);
  const [page, setPage] = useState(0);
  const itemsPerPage = 8;
  const searchTimeout = React.useRef<ReturnType<typeof setTimeout> | null>(null);
  const username = localStorage.getItem('username') || '';

  useEffect(() => {
    setLoading(true);
    Promise.all([
      getFavorites(),
      getBooks({ search: search.length >= 3 ? search : undefined, page: 0, size: 100 })
    ])
      .then(([favRes, booksRes]) => {
        setFavorites(favRes.data.map((b: any) => b.id));
        setBooks(booksRes.data.content || booksRes.data);
        setLoading(false);
      })
      .catch(err => {
        setLoading(false);
        if (err?.response?.status === 401) {
          localStorage.clear();
          navigate('/login');
        }
      });
  }, [search]);

  const handleFavorite = async (bookId: string, isFav: boolean) => {
    if (isFav) {
      await removeFavorite(bookId);
    } else {
      await addFavorite(bookId);
    }
    // Refresh favorites
    console.log('getFavorites called: after favorite change');
    getFavorites().then(res => {
      setFavorites(res.data.map((b: any) => b.id));
      console.log('getFavorites response: after favorite change', res.data);
    });
  };

  // Debounced search effect
  React.useEffect(() => {
    // Reset page to 0 only if search actually changes (not when clearing to empty)
    if (search !== '') {
      setPage(0);
    }
  }, [search]);
return (
    <Box minHeight="100vh" sx={{ backgroundColor: 'black' }}>
      <Navbar username={username} />
      <Box maxWidth="lg" mx="auto" py={10} px={4}>
        <Box display="flex" alignItems="center" justifyContent="space-between" mb={4}>
          <Typography variant="h3" fontWeight={900} color="error" sx={{ textAlign: 'left' }}>
            Book Library
          </Typography>
          <TextField
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Type 3 chars to search by title or author..."
            variant="outlined"
            autoFocus
            sx={{
              width: 450,
              bgcolor: '#181818',
              borderRadius: 4,
              boxShadow: 2,
              input: { color: 'white', fontSize: 18 },
              '& .MuiOutlinedInput-root': {
                borderRadius: 4,
                background: '#181818',
                boxShadow: '0 2px 8px rgba(0,0,0,0.12)',
                transition: 'box-shadow 0.2s',
                '&:hover': { boxShadow: '0 4px 16px rgba(229,9,20,0.15)' },
                '&.Mui-focused': {
                  boxShadow: '0 0 0 2px #e50914',
                  borderColor: '#e50914',
                },
              },
            }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon sx={{ color: '#e50914', fontSize: 28 }} />
                </InputAdornment>
              ),
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton
                    onClick={() => setSearch('')}
                    edge="end"
                    sx={{
                      opacity: search.length > 0 ? 1 : 0,
                      transition: 'opacity 0.2s',
                      color: 'white',
                    }}
                  >
                    <ClearIcon />
                  </IconButton>
                </InputAdornment>
              ),
              style: { color: 'white' },
            }}
            InputLabelProps={{ style: { color: '#e50914', fontWeight: 700, fontSize: 16 } }}
          />
        </Box>
        {/* Content area: show spinner or book list */}
        {loading ? (
          <Box display="flex" alignItems="center" justifyContent="center" height="60vh">
            <CircularProgress color="error" />
          </Box>
        ) : (
          <Grid container spacing={4}>
            {books.length === 0 && search.length >= 3 ? (
              <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" sx={{ color: 'white', bgcolor: '#181818', borderRadius: 4, boxShadow: 2, mt: 4, width: '100%' }}>
                <SearchIcon sx={{ fontSize: 48, color: '#e50914', mb: 2 }} />
                <Typography variant="h5" sx={{ mb: 1, fontWeight: 700 }}>
                  No results found
                </Typography>
                <Typography variant="body1" sx={{ mb: 2, maxWidth: 400, textAlign: 'center' }}>
                  Your search did not return any results.<br />Try modifying your search or <span style={{ color: '#e50914', cursor: 'pointer', textDecoration: 'underline' }} onClick={() => setSearch('')}>clear the search term</span>.
                </Typography>
              </Box>
            ) : null}
            {books.length > 0 && books.slice(page * itemsPerPage, (page + 1) * itemsPerPage).map(book => (
              <Grid item key={book.id} xs={12} sm={6} md={4} lg={3}>
                <BookCard
                  book={book}
                  isFavorite={favorites.includes(book.id)}
                  onFavorite={handleFavorite}
                  showFavorite={true}
                  showViewDetails={true}
                />
              </Grid>
            ))}
          </Grid>
        )}
        {books.length > itemsPerPage && (
          <Box display="flex" justifyContent="center" mt={3}>
            <Pagination
              count={Math.ceil(books.length / itemsPerPage)}
              page={page + 1}
              onChange={(_event: React.ChangeEvent<unknown>, value: number) => {
                setPage(value - 1);
              }}
              color="primary"
            />
          </Box>
        )}
      </Box>
    </Box>
  );
};
export default Books;
