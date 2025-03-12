import React, { useState, useEffect } from 'react';
import { Box, Button, TextField, Typography } from '@mui/material';
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { urlRegex, ipRegex } from '../../utils/regex';
import { verifyServerUrl } from '../../apis/server-api';
import { getKycInfo, registerKycInfo } from '../../apis/kyc-api';
import { useDialogs } from '@toolpad/core/useDialogs';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../utils/errorHandler';

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
    }
  };

  const handleTestServerConnection = async () => {
    if (!formData.serverUrl) {
      setErrors((prev) => ({ ...prev, serverUrl: 'Please enter the server URL.' }));
      setIsServerValid(false);
      return;
    }

    if (!urlRegex.test(formData.serverUrl) && !ipRegex.test(formData.serverUrl)) {
      setErrors((prev) => ({ ...prev, serverUrl: 'Please enter a valid URL.' }));
      setIsServerValid(false);
      return;
    }

    let baseUrl;
    try {
        const url = new URL(formData.serverUrl);
        baseUrl = `${url.protocol}//${url.host}`;
    } catch (error) {
        setErrors((prev) => ({ ...prev, serverUrl: 'Invalid URL format.' }));
        setIsServerValid(false);
        return;
    }

    try {
      const response = await verifyServerUrl({ serverUrl: baseUrl });
      if (response.data.isAvailable) {
        setIsServerValid(true);
        setErrors((prev) => ({ ...prev, serverUrl: undefined }));
      } else {
        setErrors((prev) => ({ ...prev, serverUrl: 'Test Connection failed.' }));
        setIsServerValid(false);
      }
    } catch (error) {
      setErrors((prev) => ({ ...prev, serverUrl: 'Error occurred while testing connection.' }));
      setIsServerValid(false);
    }
  };

  const handleReset = () => {
    setFormData(initialData);
    setIsButtonDisabled(true);
    setErrors({});
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

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <Box>
        <Box sx={{ maxWidth: 500, margin: 'auto', p: 3, border: '1px solid #ccc', borderRadius: 2 }}>
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
        </Box>

        <Box sx={{ maxWidth: 500, margin: 'auto', mt: 2, p: 3, border: '1px solid #ccc', borderRadius: 2 }}>
          <TextField
            fullWidth
            label="Name"
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
              label="Server URL"
              variant="outlined"
              margin="normal"
              value={formData.serverUrl}
              onChange={handleChange('serverUrl')}
              error={!!errors.serverUrl}
              helperText={errors.serverUrl}
              sx={{ maxLength: 200 }}
            />
            <Button 
              variant="contained" 
              onClick={handleTestServerConnection} 
              disabled={!formData.serverUrl}
              sx={{ minWidth: 150, whiteSpace: 'nowrap', textTransform: 'none' }}
            >
              Test Connection
            </Button>
          </Box>

          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
            <Button variant="contained" color="secondary" onClick={handleReset}>
              Reset
            </Button>
            <Button 
              variant="contained" 
              color="primary" 
              onClick={handleSubmit}
              disabled={isButtonDisabled}
            >
              {isEditMode ? 'Update' : 'Register'}
            </Button>
          </Box>
        </Box>
      </Box>
    </>
  );
};

export default KycSettingPage;
