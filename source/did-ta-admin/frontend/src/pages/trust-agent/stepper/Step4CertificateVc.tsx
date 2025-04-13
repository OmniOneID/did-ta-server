import {
  Box,
  Button,
  Card,
  CardContent,
  TextField,
  Typography,
  styled,
} from '@mui/material';
import React, { useEffect, useMemo, useState } from 'react';
import { generateTaCertificate, getTaCertificate, registerTaCertificate } from '../../../apis/ta-api';
import { formatErrorMessage } from '../../../utils/error-handler';

interface Props {
  step: number;
  onRegister: (step: number, validate: () => boolean, afterValidate?: () => Promise<void>) => void;
  setIsLoading: (loading: boolean) => void;
}

const Step4CertificateVC: React.FC<Props> = ({ step, onRegister, setIsLoading }) => {
  const [dn, setDn] = useState('');
  const [vcJson, setVcJson] = useState('');
  const [error, setError] = useState<string | undefined>(undefined);
  const [isCreated, setIsCreated] = useState(false);
  const [isBlockchainRegistered, setIsBlockchainRegistered] = useState(false);

  const validateDn = (value: string) => {
    if (!value.trim()) return 'DN is required.';
    if (value.length < 4 || value.length > 100) return 'DN must be between 4 and 100 characters.';
    return undefined;
  };

  const handleGenerateCertificate = async () => {
    const validationMessage = validateDn(dn);
    if (validationMessage) {
      setError(validationMessage);
      setIsCreated(false);
      return;
    }

    setError(undefined);

    const requestBody = {
      dn: dn,
    };

    setIsLoading(true);
    await generateTaCertificate(requestBody)
      .then((response) => {
        const data = response.data;
        setVcJson(JSON.stringify(data, null, 2));
        setIsCreated(true);
        setIsLoading(false);
      })
      .catch((error) => {
        setError(formatErrorMessage(error, 'Failed to generate Certificate VC'));
        setIsCreated(false);
        setIsLoading(false);
      });
  };

  const handleRegisterBlockchain = async () => {
    const requestBody = {
      certificate: vcJson,
    };

    setIsLoading(true);
    await registerTaCertificate(requestBody)
      .then((response) => {
        setIsBlockchainRegistered(true);
        setIsLoading(false);
      })
      .catch((error) => {
        setError(formatErrorMessage(error, 'Failed to register Certificate VC'));
        setIsBlockchainRegistered(false);
        setIsLoading(false);
      });
  };

  const validate = () => {
    return isBlockchainRegistered;
  };

  const afterValidate = async () => {
    console.log('Step4 afterValidate: Password ready for next step');
  };

  useEffect(() => {
    const fetchTaCertificate = () => {
      setIsLoading(true);
      getTaCertificate()
        .then(({ data }) => {
          setVcJson(JSON.stringify(data, null, 2));
          setIsCreated(true);
          setIsBlockchainRegistered(true);
          setIsLoading(false);
        })
        .catch((err) => {
          console.error('Error fetching TA certificate:', err);
          setIsLoading(false);
        });
    };

    fetchTaCertificate();
  }, []);

  useEffect(() => {
    onRegister(step, validate, afterValidate);
  }, [isBlockchainRegistered]);

  const StyledDescription = useMemo(() => styled(Box)(({ theme }) => ({
    maxWidth: 600, 
    marginTop: theme.spacing(1),
    padding: theme.spacing(0),
  })), []);

  return (
    <Box>
      <Typography variant="h6" gutterBottom>
        Step 4 – Issue Certificate VC
      </Typography>
      <StyledDescription>
        <Typography variant="body1">
          In this step, you will issue a Certificate VC for the Trust Agent.
          This credential is required for trusted communication between entities.
        </Typography>
        <Typography variant="body1" sx={{ mt: 1 }}>
          Unlike other entities, the TA issues its own Certificate VC. 
          This self-signed credential proves that the TA has been formally registered as an Entity within the OpenDID system.
        </Typography>
        <Typography variant="body1" sx={{ mt: 1 }}>
          The <strong>Distinguished Name (DN)</strong> uniquely identifies the TA within the OpenDID system.
          It must follow the LDAP DN format, for example:
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
          cn=TrustAgent,dc=example,dc=com
        </Box>
      </StyledDescription>

      {/* Generate Certificate VC */}
      <Card variant="outlined" sx={{ mb: 4, mt: 1 }}>
        <CardContent>
          <Typography variant="subtitle1" gutterBottom>
            Step 1. Generate Certificate VC
          </Typography>

          <TextField
            fullWidth
            label="DN"
            variant="outlined"
            value={dn}
            onChange={(e) => {
              setDn(e.target.value);
              setIsCreated(false);
              setIsBlockchainRegistered(false);
            }}
            error={!!error}
            helperText={error}
            sx={{ mb: 2 }}
          />

          <Button 
            variant="contained" 
            onClick={handleGenerateCertificate}
            disabled={isBlockchainRegistered}
          >
            Generate
          </Button>

          {isCreated && (
            <>
              <Box
                sx={{
                  maxHeight: 300,
                  overflow: 'auto',
                  backgroundColor: '#f5f5f5',
                  border: '1px solid #ccc',
                  borderRadius: 1,
                  padding: 2,
                  fontFamily: 'monospace',
                  whiteSpace: 'pre-wrap',
                  marginTop: 2,
                  fontSize: 14,
                }}
              >
                {vcJson}
              </Box>
                <Typography variant="body2" color="success.main" sx={{ mt: 1 }}>
                  ✅ Certificate VC has been successfully created.
                </Typography>
            </>
          )}
        </CardContent>
      </Card>

      {/* Register to Blockchain */}
      {isCreated && (
        <Card variant="outlined">
          <CardContent>
            <Typography variant="subtitle1" gutterBottom>
              Step 2. Register to Blockchain
            </Typography>
            <Button 
              variant="contained" 
              onClick={handleRegisterBlockchain}
              disabled={isBlockchainRegistered}
            >
              Register
            </Button>
            {isBlockchainRegistered && (
              <Typography variant="body2" color="success.main" sx={{ mt: 1 }}>
                ✅ Successfully registered on the blockchain.
              </Typography>
            )}
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default Step4CertificateVC;
