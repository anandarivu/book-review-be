import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#e50914', // Netflix red
    },
    background: {
      default: '#141414', // Netflix black
      paper: '#181818',
    },
    text: {
      primary: '#fff',
      secondary: '#e50914',
    },
  },
  shape: {
    borderRadius: 8,
  },
  typography: {
    fontFamily: 'Roboto, Arial, sans-serif',
    fontWeightBold: 700,
  },
});

export default theme;
