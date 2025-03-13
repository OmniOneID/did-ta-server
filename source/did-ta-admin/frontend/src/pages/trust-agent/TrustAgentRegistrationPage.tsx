import { Box, Button, SelectChangeEvent, TextField, Typography } from '@mui/material';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useState } from 'react';
import { useNavigate } from 'react-router';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../components/dialog/CustomDialog';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { useServerStatus } from '../../context/ServerStatusContext';
import { postData } from '../../utils/api';
import { ipRegex, urlRegex } from '../../utils/regex';

interface TaFormData {
  serverUrl: string;
}

interface ErrorState {
  serverUrl?: string;
}

const TrustAgentRegisterPage = () => {
  const navigate = useNavigate();
  const { setServerStatus, setTaInfo, serverStatus } = useServerStatus();
  const dialogs = useDialogs();
  const [isLoading, setIsLoading] = useState(false);
  const [formData, setFormData] = useState<TaFormData>({
    serverUrl: '',
  });
  const [errors, setErrors] = useState<ErrorState>({});
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);

  const API_BASE_URL = "/tas/admin/v1";


  const validate = () => {
    let tempErrors: ErrorState = {};
    tempErrors.serverUrl = validateServerUrl(formData.serverUrl);

    setErrors(tempErrors);
    return Object.values(tempErrors).every((error) => !error);
  };

  const validateServerUrl = (serverUrl?: string): string | undefined => {
    if (!serverUrl) return 'Please enter a Server URL.';
    if (!urlRegex.test(serverUrl) && !ipRegex.test(serverUrl)) return 'Please enter a valid URL.';
    return undefined;
};

  const handleSimpleRegistration = async () => {
    if (!validate()) return;

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to register Trust Agent?',
      isModal: true,
    });

    if (result) {
      setIsLoading(true);

      try {
        const { data } = await postData(API_BASE_URL, 'ta/register-simple', formData);
        setServerStatus(data.status);
        setTaInfo(data);
  
        if (data.status === 'COMPLETED') {
  
          await dialogs.open(CustomDialog, {
            title: 'Notification',
            message: `Registration completed successfully.`,
            isModal: true,
          },{
            onClose: async (result) =>  navigate('/ta-management'),
          });
        }
      } catch (err: any) {
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: `Failed to register TA: ${err.message}`,
          isModal: true,
        });
  
      } finally {
        setIsLoading(false);
      }
    }
  };

  const handleChange = (field: keyof TaFormData) => 
        (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<string>) => {
            const newValue = event.target.value;
            setFormData((prev) => ({ ...prev, [field]: newValue }));
  };

  // if (serverStatus === 'COMPLETED') {
  //   return <Navigate to="/ta-management" replace />;
  // }

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <Box sx={{ p: 3 }}>
        <Typography variant="h4">Trust Agent Quick Registration</Typography>
  
        <Box
          sx={{
            backgroundColor: 'white',
            p: 3,
            borderRadius: 3,
            margin: 'auto', 
            mt: 3,
            boxShadow: '0px 4px 8px 0px #0000001A',
          }}
        >
          <Typography sx={{ textAlign: 'left', fontSize: '24px', fontWeight: 700 }}>
            Trust Agent Quick Registration
          </Typography>
  
          <Box sx={{ maxWidth: 500, margin: 'auto', p: 3, pl: 0 }}>
            <Typography variant="body1" sx={{ color: '#666666' }}>
              This is a <strong>temporary registration page</strong> for the Trust Agent.
            </Typography>
            <Typography variant="body1" sx={{ mt: 1, color: '#666666' }}>
              A more detailed registration page will be updated in the second phase of development, scheduled for April.
            </Typography>
          </Box>
  
          <Box sx={{ maxWidth: 500, margin: 'auto', mt: 2, display: 'flex', alignItems: 'flex-start', gap: 2 }}>
            <TextField
              fullWidth
              label="Server URL"
              variant="outlined"
              size="small"
              value={formData.serverUrl}
              onChange={handleChange('serverUrl')}
              error={!!errors.serverUrl}
              helperText={errors.serverUrl}
              sx={{
                minWidth: 250,
                flex: 1,
                minHeight: 56,
              }}
            />

            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
              <Button variant="contained" color="primary" onClick={handleSimpleRegistration}>
                Quick Register
              </Button>
            </Box>
          </Box>
        </Box>
      </Box>
    </>
  );
  
    
};

export default TrustAgentRegisterPage;
