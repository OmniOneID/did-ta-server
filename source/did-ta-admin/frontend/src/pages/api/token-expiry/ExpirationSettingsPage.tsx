import React, { useState, useEffect, useMemo } from 'react'
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { Box, Button, styled, TextField, Typography } from '@mui/material';
import { useNavigate } from 'react-router';
import { useDialogs } from '@toolpad/core';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { getExpirationSettingInfo, registerExpirationSettingInfo } from '../../../apis/api-api';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

interface ExpirationFormData {
  tokenExpirationSeconds?: number;
  transactionExpirationSeconds?: number;
}

interface ErrorState {
  tokenExpirationSeconds?: string;
  transactionExpirationSeconds?: string;
}

const ExpirationSettingsPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  
  const [isLoading, setIsLoading] = useState(true);
  const [formData, setFormData] = useState<ExpirationFormData>({ tokenExpirationSeconds: undefined, transactionExpirationSeconds: undefined});
  const [errors, setErrors] = useState<ErrorState>({});
  const [isEditMode, setIsEditMode] = useState(false);
  const [initialData, setInitialData] = useState<ExpirationFormData>({ tokenExpirationSeconds: undefined, transactionExpirationSeconds: undefined});
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);

  useEffect(() => {
    setIsLoading(true);

    const fetchData = async () => {
      try {
        const { data } = await getExpirationSettingInfo();
        if (data?.tokenExpirationSeconds) {
          setFormData(data);
          setInitialData(data);
          setIsEditMode(true);
          setIsLoading(false);
        }
      } catch (err) {
        setIsLoading(false);
        navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch Expiration Settings") } });
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, []);

  useEffect(() => {
      const isModified = Object.keys(formData).some(
        (key) => formData[key as keyof ExpirationFormData] !== initialData[key as keyof ExpirationFormData]
      );
      setIsButtonDisabled(!isModified);
    }, [formData, initialData]);

  const handleChange = (field: keyof ExpirationFormData) => (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = event.target.value === "" ? undefined : Number(event.target.value); 
    setFormData((prev) => ({ ...prev, [field]: newValue }));
  }   

  const handleReset = () => {
    setFormData(initialData);
    setIsButtonDisabled(true);
    setErrors({});
  };

  const validate = () => {
    let tempErrors: ErrorState = {};

    if (formData.tokenExpirationSeconds === undefined || isNaN(formData.tokenExpirationSeconds)) {
      tempErrors.tokenExpirationSeconds = "Token Timeout is required.";
    } else if (formData.tokenExpirationSeconds < 10 || formData.tokenExpirationSeconds > 600) {
      tempErrors.tokenExpirationSeconds = "Token Timeout must be between 10 and 600 seconds.";
    }

    if (formData.transactionExpirationSeconds === undefined || isNaN(formData.transactionExpirationSeconds)) {
      tempErrors.transactionExpirationSeconds = "Transaction Timeout is required.";
    } else if (formData.transactionExpirationSeconds < 60 || formData.transactionExpirationSeconds > 3600) {
      tempErrors.transactionExpirationSeconds = "Transaction Timeout must be between 60 and 3600 seconds.";
    }

    setErrors(tempErrors);
    return Object.values(tempErrors).every((error) => !error);
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to register Expiration Settings?',
      isModal: true,
    });

    if (result) {
      setIsLoading(true);
      await registerExpirationSettingInfo(formData).then((response) => {
        setIsLoading(false);
        setInitialData(response.data);
        dialogs.open(CustomDialog, {
            title: 'Notification',
            message: 'Completed Expiration registration.',
            isModal: true,
        });
      }).catch((error) => {
        setIsLoading(false);
        dialogs.open(CustomDialog, {
            title: 'Notification',
            message: `Failed to register Expiration: ${error}`,
            isModal: true,
        });
      });
    }
  };

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    width: 500,
    margin: 'auto',
    marginTop: theme.spacing(1),
    padding: theme.spacing(3),
    border: 'none',
    borderRadius: theme.shape.borderRadius,
    backgroundColor: '#ffffff',
    boxShadow: '0px 4px 8px 0px #0000001A',
  })), []);
        
  const StyledSubTitle = useMemo(() => styled(Typography)({
      textAlign: 'left',
      fontSize: '24px',
      fontWeight: 700,
  }), []);
  
  const StyledDescription = useMemo(() => styled(Box)(({ theme }) => ({
      maxWidth: 500, 
      marginTop: theme.spacing(1),
      padding: theme.spacing(0),
  })), []);
  
  const StyledInputArea = useMemo(() => styled(Box)(({ theme }) => ({
      marginTop: theme.spacing(2),
  })), []);

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <StyledContainer>
        <StyledSubTitle>Expiration Settings</StyledSubTitle>
    
        <StyledDescription>
          <Typography variant="body1">
            Configure the expiration times managed by the Trust Agent.
          </Typography>
          <Typography variant="body1" sx={{ mt: 1 }}>
            <strong>Token Timeout (seconds)</strong>: Sets the expiration time for server tokens issued by the TA.
          </Typography>
          <Typography variant="body1" sx={{ mt: 1 }}>
            <strong>Transaction Timeout (seconds)</strong>: Sets the expiration time for transactions managed by the TA.
          </Typography>
        </StyledDescription>

        <StyledInputArea>
          <TextField
            fullWidth
            label="Token Timeout (seconds) *"
            variant="outlined"
            margin="normal"
            value={formData.tokenExpirationSeconds ?? ""}
            onChange={handleChange('tokenExpirationSeconds')}
            error={!!errors.tokenExpirationSeconds}
            helperText={errors.tokenExpirationSeconds}
            type='number'
            slotProps={{ htmlInput: {
              min: 10,
              max: 600,
              },
            }}
          />

          <TextField
            fullWidth
            label="Transaction Timeout (seconds) *"
            variant="outlined"
            margin="normal"
            value={formData.transactionExpirationSeconds ?? ""}
            onChange={handleChange('transactionExpirationSeconds')}
            error={!!errors.transactionExpirationSeconds}
            helperText={errors.transactionExpirationSeconds}
            type='number'
            slotProps={{ htmlInput: {
              min: 60,
              max: 3600,
              },
            }}
          />

          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
            <Button 
              variant="contained" 
              color="primary" 
              onClick={handleSubmit}
              disabled={isButtonDisabled}
            >
              {isEditMode ? 'Update' : 'Register'}
            </Button>
            <Button variant="contained" color="secondary" onClick={handleReset}>
              Reset
            </Button>
          </Box>
        </StyledInputArea>
      </StyledContainer>
    </>
  )
}

export default ExpirationSettingsPage