import React from 'react';
import { Typography, Button, Box } from '@mui/material';
import { useLocation } from 'react-router';
import error from '../assets/error.svg';

const ErrorPage: React.FC = () => {
  const location = useLocation();

  const goBackAndRefresh = () => {
    if (window.history.length > 1) {
      window.history.back();
    } else {
      window.location.href = "/";
    }
  };

  const errorMessage = location.state?.message || "An unexpected error occurred. Please try again later.";
  const errorLines: string[] = errorMessage.split("\n").filter((line: string) => line.trim() !== "Error Occurred");

  return (
    <Box
      sx={{
        width: '100%',
        height: '230px',
        paddingX: '15px',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: "#ffffff",
        borderRadius: "16px",
        marginTop: '20px'
      }}
    >
      <Typography 
        variant="h4" 
        sx={{ 
          color: "#000000", 
          lineHeight: "36px", 
          display: "flex", 
          alignItems: "center", 
          gap: 1 
        }}
        gutterBottom
      >
        <img src={error} alt="Error Icon" style={{ height: '36px', width: '36px' }} /> Error
      </Typography>

      <Box sx={{ textAlign: "center", maxWidth: "600px", wordBreak: "break-word", paddingX: "15px" }}>
        {errorLines.map((line: string, index: number) => (
          <Typography key={index} variant="body2">{line}</Typography>
        ))}
      </Box>

      <Button 
        variant="outlined" 
        color="primary" 
        onClick={goBackAndRefresh} 
        sx={{ marginTop: '20px' }}
      >
        Go Back
      </Button>
    </Box>
  );
};

export default ErrorPage;
