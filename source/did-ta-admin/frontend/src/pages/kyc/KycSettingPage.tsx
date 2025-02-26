import React, { useState } from 'react'
import CustomDialog from '../../components/dialog/CustomDialog';
import { useDialogs } from '@toolpad/core/useDialogs';
import { Box, Button, SelectChangeEvent, Typography, TextField } from '@mui/material';
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { ipRegex, urlRegex } from '../../utils/regex';
import { verifyServerUrl } from '../../apis/ServerApi';

type Props = {}

interface KycFormData {
    name?: string;
    serverUrl?: string;
}

interface ErrorState {
    name?: string;
    serverUrl?: string;
}

const KycSettingPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [isLoading, setIsLoading] = useState(false);
    const [errors, setErrors] = useState<ErrorState>({});
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);
    const [isServerValid, setIsServerValid] = useState(false);

    const [formData, setFormData] = useState<KycFormData>({
        name: '',
        serverUrl: '',
    });

     const handleChange = (field: keyof KycFormData) => 
        (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<string>) => {
            const newValue = event.target.value as string;
            setFormData((prev) => ({ ...prev, [field]: newValue }));
    
            if (field === 'name') {
                setErrors((prev) => ({ ...prev, name: undefined })); 
            }
    
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
        
            try {
                const response = await verifyServerUrl({ serverUrl: formData.serverUrl });
                if (response.data.isAvailable === false) {
                    setErrors((prev) => ({ ...prev, serverUrl: 'Test Connection failed.' }));
                    setIsServerValid(false);
                } else {
                    setIsServerValid(true);
                    setErrors((prev) => ({ ...prev, serverUrl: undefined }));
                }
            } catch (error) {
                setErrors((prev) => ({ ...prev, serverUrl: 'Error occurred while testing connection.' }));
                setIsServerValid(false);
            }
        };

    React.useEffect(() => {
        const isModified = Object.values(formData).some((value) => value !== '');
        setIsButtonDisabled(!isModified);
    }, [formData]);

    return (
        <>
            <FullscreenLoader open={isLoading} />
            <Box>
                <Box sx={{ maxWidth: 500, margin: 'auto', p: 3, border: '1px solid #ccc', borderRadius: 2 }}>
                    <Typography variant="body1">
                        The Trust Agent requires users' Personally Identifiable Information (PII) and retrieves it from a pre-integrated KYC server. 
                        In this system, the CAS (Credential Authentication Service) acts as the KYC server and provides the required PII.
                    </Typography>
                    <Typography variant="body1" sx={{ mt: 1 }}>
                        To enable KYC integration, you must configure the CA Service URL (CAS).  
                        Use the following format:
                    </Typography>
                    <Box
                    sx={{
                        backgroundColor: '#f5f5f5',
                        padding: '8px 12px',
                        borderRadius: '4px',
                        fontFamily: 'monospace',
                        display: 'inline-block',
                        mt: 1,
                    }}
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
                        sx={{minWidth: 250}}
                        slotProps={{ htmlInput: {
                                minLength: 3,
                                maxLength: 20,
                                },
                            }
                        }
                    />

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <TextField
                            fullWidth
                            label="URL"
                            variant="outlined"
                            margin="normal"
                            value={formData.serverUrl}
                            onChange={handleChange('serverUrl')}
                            error={!!errors.serverUrl}
                            helperText={errors.serverUrl}
                            sx={{minWidth: 250}}
                            slotProps={{ htmlInput: {
                                    maxLength: 200,
                                    },
                                }
                            }
                        />
                        <Button 
                            variant="contained" 
                            onClick={handleTestServerConnection} 
                            disabled={!formData.serverUrl}
                            sx={{ 
                                minWidth: 150,  
                                whiteSpace: 'nowrap',  
                                textTransform: 'none'
                            }}
                        >
                            Test Connection
                        </Button>
                    </Box>

                    <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
                        <Button variant="contained" color="primary" onClick={() => alert('click')}>Reset</Button>
                        <Button variant="contained" color="primary" onClick={() => alert('click')} disabled={isButtonDisabled}>Submit</Button>
                    </Box>

                </Box>
            </Box>
        </>
    )
}

export default KycSettingPage