import React from 'react';
import CircularProgress from '@mui/material/CircularProgress';
import Box from '@mui/material/Box';

const LoadingScreen: React.FC = () => {
  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh', 
        width: '100vw', 
        position: 'fixed', 
        top: 0,
        left: 0,
        backgroundColor: 'rgba(255, 255, 255, 0.8)', 
        zIndex: 1300,
      }}
    >
      <CircularProgress size={80} />
    </Box>
  );
};

export default LoadingScreen;
