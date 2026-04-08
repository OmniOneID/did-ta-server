import React, {useState, useEffect, useMemo} from 'react';
import {
    Box,
    Button,
    styled,
    TextField,
    Typography,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    FormHelperText
} from '@mui/material';
import {useNavigate} from 'react-router';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import {urlRegex, ipRegex} from '../../utils/regex';
import {verifyServerUrl} from '../../apis/server-api';
import {getKycInfo, registerKycInfo} from '../../apis/kyc-api';
import {useDialogs} from '@toolpad/core/useDialogs';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../components/dialog/CustomDialog';
import {formatErrorMessage} from '../../utils/error-handler';
import {getEntitiesByRole} from '../../apis/entity-api';

interface KycFormData {
    name?: string;
    kycVerificationType?: 'TOKEN' | 'TRANSACTION';
    serverUrl?: string;
    signerDid?: string;
}

interface ErrorState {
    name?: string;
    kycVerificationType?: string;
    serverUrl?: string;
    signerDid?: string;
}

interface Entity {
    id: number;
    name: string;
    did: string;
}

const KycSettingPage: React.FC = () => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [isLoading, setIsLoading] = useState(true);
    const [formData, setFormData] = useState<KycFormData>({name: '', kycVerificationType: 'TOKEN', serverUrl: '', signerDid: ''});
    const [initialData, setInitialData] = useState<KycFormData>({name: '', kycVerificationType: 'TOKEN', serverUrl: '', signerDid: ''});
    const [errors, setErrors] = useState<ErrorState>({});
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);
    const [isServerValid, setIsServerValid] = useState(false);
    const [serverCheckMessage, setServerCheckMessage] = useState<string>('');
    const [serverCheckStatus, setServerCheckStatus] = useState<'success' | 'error' | ''>('');
    const [isEditMode, setIsEditMode] = useState(false);
    const [didEntities, setDidEntities] = useState<Entity[]>([]);

    useEffect(() => {
        const fetchDidEntities = async () => {
            try {
                const {data} = await getEntitiesByRole('OP_PROVIDER');
                setDidEntities(data || []);
            } catch (err) {
                setDidEntities([]);
                setIsLoading(false);
            }
        };
        fetchDidEntities();
    }, []);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const {data} = await getKycInfo();

                if (data?.id) {
                    let verificationType: 'TOKEN' | 'TRANSACTION' = 'TOKEN';

                    const typeValue = data.type || data.kycVerificationType || data.verificationType;

                    if (typeValue) {
                        const normalizedType = typeValue.toString().toUpperCase();
                        if (normalizedType === 'TRANSACTION') {
                            verificationType = 'TRANSACTION';
                        } else if (normalizedType === 'TOKEN') {
                            verificationType = 'TOKEN';
                        }
                    }

                    let signerDidValue = 'ALL';
                    if (data.signerDid && data.signerDid !== '' && data.signerDid !== null) {
                        signerDidValue = data.signerDid;
                    } else if (data.did && data.did !== '' && data.did !== null) {
                        signerDidValue = data.did;
                    }

                    const kycData: KycFormData = {
                        name: data.name || '',
                        kycVerificationType: verificationType,
                        serverUrl: data.serverUrl || '',
                        signerDid: signerDidValue
                    };

                    setFormData(kycData);
                    setInitialData(kycData);
                    setIsEditMode(true);
                }
            } catch (err) {
                navigate('/error', {state: {message: formatErrorMessage(err, "Failed to fetch KYC Server Settings")}});
            } finally {
                setIsLoading(false);
            }
        };

        fetchData();
    }, [navigate]);

    useEffect(() => {
        const isModified = JSON.stringify(formData) !== JSON.stringify(initialData);
        setIsButtonDisabled(!isModified);
    }, [formData, initialData]);

    const handleChange = (field: keyof KycFormData) => (event: React.ChangeEvent<HTMLInputElement> | any) => {
        const newValue = event.target.value;

        if (field === 'kycVerificationType') {
            setFormData((prev) => ({
                ...prev,
                [field]: newValue,
                serverUrl: newValue === 'TRANSACTION' ? prev.serverUrl : '',
                signerDid: newValue === 'TOKEN' ? prev.signerDid : 'ALL'
            }));

            setErrors((prev) => ({...prev, serverUrl: undefined, signerDid: undefined}));
            setServerCheckMessage('');
            setServerCheckStatus('');
            setIsServerValid(false);
        } else {
            setFormData((prev) => ({...prev, [field]: newValue}));
        }

        if (field === 'serverUrl') {
            setIsServerValid(false);
            setErrors((prev) => ({...prev, serverUrl: undefined}));
            setServerCheckMessage('');
            setServerCheckStatus('');
        }
    };

    const handleTestServerConnection = async () => {
        if (!formData.serverUrl) {
            setErrors((prev) => ({...prev, serverUrl: 'Please enter the server URL.'}));
            setIsServerValid(false);
            setServerCheckMessage('Please enter the server URL.');
            setServerCheckStatus('error');
            return;
        }

        if (!urlRegex.test(formData.serverUrl) && !ipRegex.test(formData.serverUrl)) {
            setErrors((prev) => ({...prev, serverUrl: 'Please enter a valid URL.'}));
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
            setErrors((prev) => ({...prev, serverUrl: 'Invalid URL format.'}));
            setIsServerValid(false);
            setServerCheckMessage('Invalid URL format.');
            setServerCheckStatus('error');
            return;
        }

        try {
            const response = await verifyServerUrl({serverUrl: baseUrl});
            if (response.data.isAvailable) {
                setIsServerValid(true);
                setErrors((prev) => ({...prev, serverUrl: undefined}));
                setServerCheckMessage('Server connection test successful.');
                setServerCheckStatus('success');
            } else {
                setErrors((prev) => ({...prev, serverUrl: 'Test Connection failed.'}));
                setIsServerValid(false);
                setServerCheckMessage('Test connection failed. Please check the server URL.');
                setServerCheckStatus('error');
            }
        } catch (error) {
            setErrors((prev) => ({...prev, serverUrl: 'Error occurred while testing connection.'}));
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

        tempErrors.name = validateName(formData.name);
        tempErrors.kycVerificationType = validateType(formData.kycVerificationType);

        if (formData.kycVerificationType === 'TRANSACTION') {
            tempErrors.serverUrl = validateServerUrl(formData.serverUrl);
        } else if (formData.kycVerificationType === 'TOKEN') {
            tempErrors.signerDid = validateDid(formData.signerDid);
        }

        setErrors(tempErrors);
        return Object.values(tempErrors).every((error) => !error);
    };

    const validateName = (name?: string): string | undefined => {
        if (!name) return 'Please enter a name.';
        if (name.length < 3 || name.length > 20) return 'Name must be between 3 and 20 characters.';
        return undefined;
    };

    const validateType = (type?: string): string | undefined => {
        if (!type) return 'Please select a type.';
        if (!['TOKEN', 'TRANSACTION'].includes(type)) return 'Please select a valid type.';
        return undefined;
    };

    const validateDid = (did?: string): string | undefined => {
        if (!did) return 'Please select a DID.';
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

            try {
                const submitData = {
                    name: formData.name,
                    kycVerificationType: formData.kycVerificationType,
                    serverUrl: formData.kycVerificationType === 'TRANSACTION' ? formData.serverUrl : '',
                    signerDid: formData.kycVerificationType === 'TOKEN' && formData.signerDid !== 'ALL' ? formData.signerDid : null
                };

                const response = await registerKycInfo(submitData);

                if (response.data) {
                    setInitialData(formData);
                }

                setIsLoading(false);
                await dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: 'Completed kyc registration.',
                    isModal: true,
                });

            } catch (error) {
                setIsLoading(false);
                await dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: `Failed to register KYC: ${formatErrorMessage(error, 'Registration failed')}`,
                    isModal: true,
                });
            } finally {
                setIsLoading(false);
            }
        }
    };

    const StyledContainer = useMemo(() => styled(Box)(({theme}) => ({
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

    const StyledDescription = useMemo(() => styled(Box)(({theme}) => ({
        maxWidth: 500,
        marginTop: theme.spacing(1),
        padding: theme.spacing(0),
    })), []);

    const StyledInputArea = useMemo(() => styled(Box)(({theme}) => ({
        marginTop: theme.spacing(2),
    })), []);

    return (
        <>
            <FullscreenLoader open={isLoading}/>
            <StyledContainer>
                <StyledSubTitle>KYC Settings</StyledSubTitle>

                <StyledDescription>
                    <Typography variant="body1">
                        The Trust Agent requires users' Personally Identifiable Information (PII) and retrieves it from
                        a pre-integrated KYC server.
                    </Typography>
                    <Typography variant="body1" sx={{mt: 1}}>
                        To enable Transaction-Based KYC integration, configure the CA Service URL (CAS) using the following format:
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
                        sx={{minLength: 3, maxLength: 20}}
                    />

                    <FormControl fullWidth margin="normal" error={!!errors.kycVerificationType}>
                        <InputLabel>Type *</InputLabel>
                        <Select
                            value={formData.kycVerificationType || ''}
                            onChange={handleChange('kycVerificationType')}
                            label="Type *"
                        >
                            <MenuItem value="TOKEN">Token-Based</MenuItem>
                            <MenuItem value="TRANSACTION">Transaction-Based</MenuItem>
                        </Select>
                        {errors.kycVerificationType && <FormHelperText>{errors.kycVerificationType}</FormHelperText>}
                    </FormControl>

                    {formData.kycVerificationType === 'TOKEN' && (
                        <FormControl fullWidth margin="normal" error={!!errors.signerDid}>
                            <InputLabel>Token Signer DID *</InputLabel>
                            <Select
                                value={formData.signerDid || ''}
                                onChange={handleChange('signerDid')}
                                label="Token Signer DID *"
                            >
                                <MenuItem value="ALL">All</MenuItem>
                                {didEntities.map((entity) => (
                                    <MenuItem key={entity.id} value={entity.did}>
                                        {entity.name}
                                    </MenuItem>
                                ))}
                            </Select>
                            {errors.signerDid && <FormHelperText>{errors.signerDid}</FormHelperText>}
                        </FormControl>
                    )}

                    {formData.kycVerificationType === 'TRANSACTION' && (
                        <Box sx={{display: 'flex', alignItems: 'center', gap: 2}}>
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
                                sx={{minWidth: 150, whiteSpace: 'nowrap', textTransform: 'none'}}
                            >
                                Test Connection
                            </Button>
                        </Box>
                    )}

                    <Box sx={{display: 'flex', justifyContent: 'center', gap: 2, mt: 3}}>
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
