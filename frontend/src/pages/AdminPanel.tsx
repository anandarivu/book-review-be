import React, { useEffect, useState, useRef } from 'react';
import { useBookStore } from '../store/bookStore';
import { getBooks } from '../api/books';
import { useNavigate } from 'react-router-dom';
import { handleJwtExpired } from '../utils/session';
import { useAuthStore } from '../store/authStore';
import { Box, Paper, Typography, CircularProgress } from '@mui/material';
import AdminPanelHeader from '../components/AdminPanelHeader';
import SearchBar from '../components/SearchBar';
import BookFormDialog from '../components/BookFormDialog';
import BookList from '../components/BookList';
import PaginationBar from '../components/PaginationBar';

const AdminPanel: React.FC = () => {
  const navigate = useNavigate();
  const { logout } = useAuthStore();
  const searchInputRef = useRef<HTMLInputElement>(null);
  const debounceRef = useRef<number | null>(null);
  const [searchInput, setSearchInput] = useState('');
  const [addOpen, setAddOpen] = useState(false);
  const { addBook, editBook, removeBook } = useBookStore();
  const [books, setBooks] = useState<any[]>([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const pageSize = 5;
  const [totalPages, setTotalPages] = useState(0);
  const { role } = useAuthStore();
  const [newBook, setNewBook] = useState({ title: '', author: '', description: '', coverImageUrl: '', genres: '', publishedYear: '' });
  const [editId, setEditId] = useState<number | null>(null);
  const [editBookData, setEditBookData] = useState({ title: '', author: '', description: '', coverImageUrl: '', genres: [], publishedYear: '' });
  const [loading, setLoading] = useState(true);
  const [reviewsDialogOpen, setReviewsDialogOpen] = useState(false);
  const [selectedBookId, setSelectedBookId] = useState<string | null>(null);

  useEffect(() => {
    if (search.length === 0 || search.length >= 3) {
      setLoading(true);
      getBooks({ search, page, size: pageSize })
        .then(res => {
          setBooks(res.data.content || res.data);
          setTotalPages(res.data.totalPages || 1);
        })
        .catch(err => {
          if (err?.response?.status === 401) {
            handleJwtExpired(navigate);
          }
        })
        .finally(() => setLoading(false));
    }
  }, [search, page, pageSize]);

  if (!role || role !== 'ADMIN')
    return (
      <Box display="flex" alignItems="center" justifyContent="center" minHeight="60vh">
        <Typography color="error" variant="h5">Access Denied</Typography>
      </Box>
    );

  const handleSearchInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    setSearchInput(val);
    setPage(0);
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = window.setTimeout(() => {
      setSearch(val);
    }, 400);
  };

  const handleAddBook = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await addBook({
        ...newBook,
        publishedYear: newBook.publishedYear ? Number(newBook.publishedYear) : undefined,
        genres: newBook.genres.split(',').map((g: string) => g.trim())
      });
      // Refresh books after add
      await getBooks({ search, page, size: pageSize })
        .then(res => {
          setBooks(res.data.content || res.data);
          setTotalPages(res.data.totalPages || 1);
        })
        .catch(err => {
          if (err?.response?.status === 401) {
            handleJwtExpired(navigate);
          }
        });
      setAddOpen(false);
      setNewBook({ title: '', author: '', description: '', coverImageUrl: '', genres: '', publishedYear: '' });
    } catch (err: any) {
      if (err?.response?.status === 401) {
        handleJwtExpired(navigate);
      }
    }
  };

  const handleEditBook = async (e: React.FormEvent) => {
    e.preventDefault();
    if (editId) {
      try {
        await editBook(editId, {
          ...editBookData,
          publishedYear: editBookData.publishedYear ? Number(editBookData.publishedYear) : undefined,
          genres: editBookData.genres.map((g: string) => g.trim())
        });
        // Refresh books after edit
        await getBooks({ search, page, size: pageSize })
          .then(res => {
            setBooks(res.data.content || res.data);
          })
          .catch(err => {
            if (err?.response?.status === 401) {
              handleJwtExpired(navigate);
            }
          });
        setEditId(null);
        setEditBookData({ title: '', author: '', description: '', coverImageUrl: '', genres: [], publishedYear: '' });
      } catch (err: any) {
        if (err?.response?.status === 401) {
          handleJwtExpired(navigate);
        }
      }
    }
  };

  const handleRemoveBook = async (id: number) => {
    try {
      await removeBook(id);
      // Refresh books after delete
      await getBooks({ search, page, size: pageSize })
        .then(res => {
          setBooks(res.data.content || res.data);
          setTotalPages(res.data.totalPages || 1);
        })
        .catch(err => {
          if (err?.response?.status === 401) {
            handleJwtExpired(navigate);
          }
        });
    } catch (err: any) {
      if (err?.response?.status === 401) {
        handleJwtExpired(navigate);
      }
    }
  };

  const handleEdit = (book: any) => {
    setEditId(book.id);
    setEditBookData({
      title: book.title,
      author: book.author,
      description: book.description,
      coverImageUrl: book.coverImageUrl,
      genres: book.genres,
      publishedYear: book.publishedYear ? String(book.publishedYear) : ''
    });
  };

  const handleOpenReviews = (id: string) => {
    setSelectedBookId(id);
    setReviewsDialogOpen(true);
  };

  if (loading)
    return (
      <Box display="flex" alignItems="center" justifyContent="center" minHeight="60vh">
        <CircularProgress color="error" />
      </Box>
    );

  return (
    <Box maxWidth="lg" mx="auto" py={4}>
      {/* Header row: Admin Panel left, Avatar right */}
      <Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
        <Typography variant="h4" fontWeight={900} color="error" sx={{ ml: 1 }}>
          Admin Panel
        </Typography>
        <AdminPanelHeader onLogout={logout} />
      </Box>
      <Paper elevation={8} sx={{ bgcolor: '#181818', p: 4, border: '2px solid #e50914' }}>
        <SearchBar
          searchInput={searchInput}
          onSearchInputChange={handleSearchInputChange}
          searchInputRef={searchInputRef}
          onAddBook={() => setAddOpen(true)}
        />
        <BookFormDialog
          open={addOpen || !!editId}
          onClose={() => {
            setAddOpen(false);
            setEditId(null);
            setEditBookData({ title: '', author: '', description: '', coverImageUrl: '', genres: [], publishedYear: '' });
          }}
          onSubmit={addOpen ? handleAddBook : handleEditBook}
          isEdit={!!editId}
          bookData={addOpen ? newBook : editBookData}
          setBookData={addOpen ? setNewBook : setEditBookData}
        />
        <Typography variant="h6" color="error" fontWeight={700} gutterBottom>
          Manage Books
        </Typography>
        <BookList
          books={books}
          onEdit={handleEdit}
          onDelete={handleRemoveBook}
          onOpenReviews={handleOpenReviews}
          selectedBookId={selectedBookId}
          reviewsDialogOpen={reviewsDialogOpen}
          setReviewsDialogOpen={setReviewsDialogOpen}
        />
        <PaginationBar
          page={page}
          totalPages={totalPages}
          onPrev={() => setPage(page - 1)}
          onNext={() => setPage(page + 1)}
        />
      </Paper>
    </Box>
  );
};

export default AdminPanel;
