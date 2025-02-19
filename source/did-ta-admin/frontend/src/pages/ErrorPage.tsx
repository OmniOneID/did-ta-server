import React from 'react';
import { Typography, Button, Box } from '@mui/material';

const ErrorPage: React.FC = () => {
  const goBackAndRefresh = () => {
    window.history.back(); // ✅ 이전 페이지로 이동
    setTimeout(() => {
        window.location.href = '/';
    }, 100); // 약간의 지연 추가 (이전 페이지 이동 후 새로고침)
  };

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      height="100vh"
      textAlign="center"
    >
      <Typography variant="h4" color="error" gutterBottom>
        Error
      </Typography>
      <Typography variant="body1" gutterBottom>
        An unexpected error occurred. Please try again later.
      </Typography>
      <Button variant="contained" color="primary" onClick={goBackAndRefresh}>
        Go Back
      </Button>
    </Box>
  );
};

export default ErrorPage;
