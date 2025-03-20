import { Box, Button, FormControl, FormHelperText, InputLabel, MenuItem, Select, SelectChangeEvent, styled, Typography } from '@mui/material'
import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import { cipherTypes } from '../../../constants/cipher-types';
import { paddingTypes } from '../../../constants/padding-types';
import { getKeyExchangePolicyInfo, registerKeyExchangePolicyInfo } from '../../../apis/api-api';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

interface KeyExchangePolicyFormData {
  cipherType: string;
  paddingType?: string;
}

interface ErrorState {
  cipherType?: string;
  paddingType?: string;
}

const KeyExchangePolicyPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();

  const [isLoading, setIsLoading] = useState(true);

  const [formData, setFormData] = useState<KeyExchangePolicyFormData>({
    cipherType: '',
    paddingType: '',
  });

  const [errors, setErrors] = useState<ErrorState>({});
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  const [initialData, setInitialData] = useState<KeyExchangePolicyFormData>({ cipherType: '', paddingType: ''});
  const [isEditMode, setIsEditMode] = useState(false);

  const handleChange = (field: keyof KeyExchangePolicyFormData) => 
    (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<string>) => {
        const newValue = event.target.value as string;
        setFormData((prev) => ({ ...prev, [field]: newValue }));
  };

  const handleReset = () => {
    setFormData(initialData);
    setIsButtonDisabled(true);
    setErrors({});
  };

  const validate = () => {
    let tempErrors: ErrorState = {};

    if (!formData.cipherType) tempErrors.cipherType = 'Please select a Cipher Type.';
    if (!formData.paddingType) tempErrors.paddingType = 'Please select a Padding Type.';

    setErrors(tempErrors);
    return Object.values(tempErrors).every((error) => !error);
  };
  const handleSubmit = async () => {
    if (!validate()) return;

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to register Key Exchange Policy Settings?',
      isModal: true,
    });

    if (result) {
      setIsLoading(true);
      await registerKeyExchangePolicyInfo(formData).then((response) => {
        setIsLoading(false);
        setInitialData(response.data);
        dialogs.open(CustomDialog, {
            title: 'Notification',
            message: 'Completed Key Exchange Policy registration.',
            isModal: true,
        });
      }).catch((error) => {
        setIsLoading(false);
        dialogs.open(CustomDialog, {
            title: 'Notification',
            message: `Failed to register Key Exchange Policy: ${error}`,
            isModal: true,
        });
      });
    }

  };

  useEffect(() => {
    setIsLoading(true);

    const fetchData = async () => {
          try {
            const { data } = await getKeyExchangePolicyInfo();
            if (data?.cipherType) {
              setFormData(data);
              setInitialData(data);
              setIsEditMode(true);
              setIsLoading(false);
            }
          } catch (err) {
            setIsLoading(false);
            navigate('/error', { state: { message: formatErrorMessage(err, "Failed to retrieve Key Exchange Policy Settings") } });
          } finally {
            setIsLoading(false);
          }
        };
        fetchData();
  }, []);


  useEffect(() => {
    const isModified = Object.keys(formData).some(
      (key) => formData[key as keyof KeyExchangePolicyFormData] !== initialData[key as keyof KeyExchangePolicyFormData]
    );
    setIsButtonDisabled(!isModified);
  }, [formData, initialData]);
  
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
        <StyledSubTitle>Key Exchange Policy</StyledSubTitle>

        <StyledDescription>
          <Typography variant="body1">
            Configure the key exchange policy used by the Trust Agent for ECDH key exchange with external entities.
          </Typography>
          <Typography variant="body1" sx={{ mt: 1 }}>
            <strong>Cipher Type</strong>: Specifies the encryption algorithm used during the key exchange process.
          </Typography>
          <Typography variant="body1" sx={{ mt: 1 }}>
            <strong>Padding Type</strong>: Defines the padding scheme applied to the encrypted data during the key exchange.
          </Typography>
        </StyledDescription>
        
        <StyledInputArea>
          <FormControl fullWidth margin="normal" error={!!errors.cipherType}>
            <InputLabel>Cipher Type *</InputLabel>
            <Select value={formData.cipherType ? formData.cipherType : ''} onChange={handleChange('cipherType')} label="Cipher Type">
            {cipherTypes.map((cipherType) => (
                <MenuItem key={cipherType.value} value={cipherType.value}>
                    {cipherType.label}
                </MenuItem>
            ))}
            </Select>
            {errors.cipherType && <FormHelperText>{errors.cipherType}</FormHelperText>}
          </FormControl>

          <FormControl fullWidth margin="normal" error={!!errors.paddingType}>
            <InputLabel>Padding Type *</InputLabel>
            <Select value={formData.paddingType ? formData.paddingType : ''} onChange={handleChange('paddingType')} label="Padding Type">
            {paddingTypes.map((paddingType) => (
                <MenuItem key={paddingType.value} value={paddingType.value}>
                    {paddingType.label}
                </MenuItem>
            ))}
            </Select>
            {errors.paddingType && <FormHelperText>{errors.paddingType}</FormHelperText>}
          </FormControl>

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

export default KeyExchangePolicyPage