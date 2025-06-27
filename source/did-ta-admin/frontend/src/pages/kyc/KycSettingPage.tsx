import React, { useState, useEffect, useMemo } from 'react';
import { Box, Button, styled, TextField, Typography } from '@mui/material';
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { urlRegex, ipRegex } from '../../utils/regex';
import { verifyServerUrl } from '../../apis/server-api';
import { getKycInfo, registerKycInfo } from '../../apis/kyc-api';
import { useDialogs } from '@toolpad/core/useDialogs';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../utils/error-handler';

interface KycFormData {
  name?: string;
  serverUrl?: string;
}

interface ErrorState {
  name?: string;
  serverUrl?: string;
}

const KycSettingPage: React.FC = () => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [isLoading, setIsLoading] = useState(true);
  const [formData, setFormData] = useState<KycFormData>({ name: '', serverUrl: '' });
  const [initialData, setInitialData] = useState<KycFormData>({ name: '', serverUrl: '' });
  const [errors, setErrors] = useState<ErrorState>({});
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  const [isServerValid, setIsServerValid] = useState(false);
  const [serverCheckMessage, setServerCheckMessage] = useState<string>('');
  const [serverCheckStatus, setServerCheckStatus] = useState<'success' | 'error' | ''>('');
  const [isEditMode, setIsEditMode] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data } = await getKycInfo();
        if (data?.id) {
          setFormData(data);
          setInitialData(data);
          setIsEditMode(true);
        }
      } catch (err) {
        setIsLoading(false);
        navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch KYC Server Settings") } });
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, []);

  useEffect(() => {
    const isModified = JSON.stringify(formData) !== JSON.stringify(initialData);
    setIsButtonDisabled(!isModified);
  }, [formData, initialData]);

  const handleChange = (field: keyof KycFormData) => (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = event.target.value;
    setFormData((prev) => ({ ...prev, [field]: newValue }));

    if (field === 'serverUrl') {
      setIsServerValid(false);
      setErrors((prev) => ({ ...prev, serverUrl: undefined }));
      setServerCheckMessage('');
      setServerCheckStatus('');
    }
  };

  const handleTestServerConnection = async () => {
    if (!formData.serverUrl) {
      setErrors((prev) => ({ ...prev, serverUrl: 'Please enter the server URL.' }));
      setIsServerValid(false);
      setServerCheckMessage('Please enter the server URL.');
      setServerCheckStatus('error');
      return;
    }

    if (!urlRegex.test(formData.serverUrl) && !ipRegex.test(formData.serverUrl)) {
      setErrors((prev) => ({ ...prev, serverUrl: 'Please enter a valid URL.' }));
      setIsServerValid(false);
      setServerCheckMessage('Please enter a valid URL.');
      setServerCheckStatus('error');
      return;
    }

    let baseUrl;
    try {
        const url = new URL(formData.serverUrl);
        baseUrl = `${url.protocol}//${url.host}`;
    } catch (error) {
        setErrors((prev) => ({ ...prev, serverUrl: 'Invalid URL format.' }));
        setIsServerValid(false);
        setServerCheckMessage('Invalid URL format.');
        setServerCheckStatus('error');
        return;
    }

    try {
      const response = await verifyServerUrl({ serverUrl: baseUrl });
      if (response.data.isAvailable) {
        setIsServerValid(true);
        setErrors((prev) => ({ ...prev, serverUrl: undefined }));
        setServerCheckMessage('Server connection test successful.');
        setServerCheckStatus('success');
      } else {
        setErrors((prev) => ({ ...prev, serverUrl: 'Test Connection failed.' }));
        setIsServerValid(false);
        setServerCheckMessage('Test connection failed. Please check the server URL.');
        setServerCheckStatus('error');
      }
    } catch (error) {
      setErrors((prev) => ({ ...prev, serverUrl: 'Error occurred while testing connection.' }));
      setIsServerValid(false);
      setServerCheckMessage('Error occurred while testing connection. Please try again.');
      setServerCheckStatus('error');
    }
  };

  const handleReset = () => {
    setFormData(initialData);
    setIsButtonDisabled(true);
    setErrors({});
    setServerCheckMessage('');
    setServerCheckStatus('');
  };

  const validate = () => {
    let tempErrors: ErrorState = {};

    // Validate Name
    tempErrors.name = validateName(formData.name);

    // Validate Server URL
    tempErrors.serverUrl = validateServerUrl(formData.serverUrl);

    setErrors(tempErrors);
    return Object.values(tempErrors).every((error) => !error);
  };

  const validateName = (name?: string): string | undefined => {
    if (!name) return 'Please enter a name.';
    if (name.length < 3 || name.length > 20) return 'Name must be between 3 and 20 characters.';
    return undefined;
  };

  const validateServerUrl = (serverUrl?: string): string | undefined => {
    if (!serverUrl) return 'Please enter the server URL.';
    if (!urlRegex.test(serverUrl) && !ipRegex.test(serverUrl)) return 'Please enter a valid URL.';
    if (serverUrl.length > 200) return 'URL must be less than 200 characters.';
    if (!isServerValid) return 'Please test the server connection.';
    return undefined;
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    const result = await dialogs.open(CustomConfirmDialog, {
        title: 'Confirmation',
        message: 'Are you sure you want to register KYC?',
        isModal: true,
      });

      if (result) {
        setIsLoading(true);
        await registerKycInfo(formData).then((response) => {
            setIsLoading(false);
            setInitialData(response.data);
            dialogs.open(CustomDialog, {
                title: 'Notification',
                message: 'Completed kyc registration.',
                isModal: true,
            });

        }).catch((error) => {
            setIsLoading(false);
            dialogs.open(CustomDialog, {
                title: 'Notification',
                message: `Failed to register KYC: ${error}`,
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
        <StyledSubTitle>KYC Settings</StyledSubTitle>

        <StyledDescription>
          <Typography variant="body1">
            The Trust Agent requires users' Personally Identifiable Information (PII) and retrieves it from a pre-integrated KYC server.
          </Typography>
          <Typography variant="body1" sx={{ mt: 1 }}>
            To enable KYC integration, you must configure the CA Service URL (CAS).  
            Use the following format:
          </Typography>
          <Box
            sx={(theme) => ({
                backgroundColor: theme.palette.mode === 'dark' ? '#333' : '#f5f5f5',
                color: theme.palette.mode === 'dark' ? '#fff' : '#000',
                padding: '8px 12px',
                borderRadius: '4px',
                fontFamily: 'monospace',
                display: 'inline-block',
                mt: 1,
                border: `1px solid ${theme.palette.divider}`,
              })}
            >
            http://{'{IP}'}:8094/cas
          </Box>
        </StyledDescription>

        <StyledInputArea>
          <TextField
            fullWidth
            label="Name *"
            variant="outlined"
            margin="normal"
            value={formData.name}
            onChange={handleChange('name')}
            error={!!errors.name}
            helperText={errors.name}
            sx={{ minLength: 3, maxLength: 20 }}
          />

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <TextField
              fullWidth
              label="Server URL *"
              variant="outlined"
              margin="normal"
              value={formData.serverUrl}
              onChange={handleChange('serverUrl')}
              error={!!errors.serverUrl}
              helperText={errors.serverUrl || serverCheckMessage}
              sx={{ 
                maxLength: 200,
                '& .MuiFormHelperText-root': {
                  color: serverCheckStatus === 'success' ? 'green' : 
                         serverCheckStatus === 'error' ? 'red' : 'inherit',
                  fontWeight: serverCheckStatus ? 500 : 'inherit'
                }
              }}
            />
            <Button 
              variant="outlined" 
              onClick={handleTestServerConnection} 
              disabled={!formData.serverUrl}
              sx={{ minWidth: 150, whiteSpace: 'nowrap', textTransform: 'none' }}
            >
              Test Connection
            </Button>
          </Box>

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
  );
};

export default KycSettingPage;
