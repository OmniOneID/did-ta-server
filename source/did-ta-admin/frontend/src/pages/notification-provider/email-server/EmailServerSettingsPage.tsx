import { Visibility, VisibilityOff } from '@mui/icons-material';
import { Box, Button, FormControl, FormHelperText, IconButton, InputAdornment, InputLabel, OutlinedInput, styled, Switch, TextField, Typography } from '@mui/material';
import { useDialogs } from '@toolpad/core';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import { getEmailServerInfo, registerEmailServerInfo, sendTestEmail } from '../../../apis/noti-api';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { emailRegex, hostRegex, portRegex } from '../../../utils/regex';
import TestEmailDialog from './TestEmailDialog';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

interface EmailServerFormData {
    host?: string;
    port?: number;
    username?: string;
    password?: string;
    startTlsEnabled?: boolean;
    sslEnabled?: boolean;
    connectionTimeout?: number;
    readTimeout?: number;
    writeTimeout?: number;
    sender?: string;
    ignoreSslValidation?: boolean;
}

interface ErrorState {
    host?: string;
    port?: string;
    username?: string;
    password?: string;
    startTlsEnabled?: string;
    sslEnabled?: string;
    connectionTimeout?: string;
    readTimeout?: string;
    writeTimeout?: string;
    sender?: string;
    ignoreSslValidation?: string;
}

const EmailServerSettingsPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();

    const [isLoading, setIsLoading] = useState(true);
    const [formData, setFormData] = useState<EmailServerFormData>({ host: undefined, port: undefined, username: undefined, password: undefined, startTlsEnabled: true, sslEnabled: false, connectionTimeout: undefined, readTimeout: undefined, writeTimeout: undefined, sender: undefined, ignoreSslValidation: false });
    const [errors, setErrors] = useState<ErrorState>({});
    const [isEditMode, setIsEditMode] = useState(false);
    const [initialData, setInitialData] = useState<EmailServerFormData>({ host: undefined, port: undefined, username: undefined, password: undefined, startTlsEnabled: true, sslEnabled: false, connectionTimeout: undefined, readTimeout: undefined, writeTimeout: undefined, sender: undefined, ignoreSslValidation: false });
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);
    const [showPassword, setShowPassword] = useState(false);
    const [isTestDialogOpen, setIsTestDialogOpen] = useState(false);

    const handleChange = (field: keyof EmailServerFormData) => (event: React.ChangeEvent<HTMLInputElement>, checked?: boolean) => {
        const newValue = event.target.type === "checkbox" || typeof checked === "boolean" ? checked ?? event.target.checked : event.target.value;
        setFormData((prev) => ({ ...prev, [field]: newValue }));
    };
    
    
    const handleReset = () => {
        setFormData(initialData);
        setIsButtonDisabled(true);
        setErrors({});
    };

    const validate = () => {
        let tempErrors: ErrorState = {};

        tempErrors.host = validateHost(formData.host);
        tempErrors.port = validatePort(formData.port);
        tempErrors.username = validateUsername(formData.username);
        tempErrors.password = validatePassword(formData.password);
        tempErrors.sender = validateSender(formData.sender);
        tempErrors.startTlsEnabled = validateEnableStartTls(formData.startTlsEnabled);
        tempErrors.sslEnabled = validateEnableSsl(formData.sslEnabled);
        if (formData.startTlsEnabled === formData.sslEnabled) {
            tempErrors.startTlsEnabled = "STARTTLS and SSL cannot be both enabled or both disabled.";
            tempErrors.sslEnabled = "STARTTLS and SSL cannot be both enabled or both disabled.";
        }
        tempErrors.connectionTimeout = validateConnectionTimeout(formData.connectionTimeout);
        tempErrors.readTimeout = validateReadTimeout(formData.readTimeout);
        tempErrors.writeTimeout = validateWriteTimeout(formData.writeTimeout);
        tempErrors.ignoreSslValidation = validateIgnoreSslValidation(formData.ignoreSslValidation);

        setErrors(tempErrors);
        return Object.values(tempErrors).every((error) => !error);
    };

    const handleClickShowPassword = () => {
        setShowPassword((prev) => !prev);
      };

    const validateHost = (host?: string): string | undefined => {
        if (!host) return 'Please enter a host.';
        if (host.length > 200) return 'Host must be less than 200 characters.';
        if (!hostRegex.test(host)) return 'Please enter a valid host.';
        return undefined;
    };

    const validatePort = (port?: number): string | undefined => {
        if (!port) return 'Please enter a port.';
        if (port < 1 || port > 65535) return 'Port must be between 1 and 65535.';
        if (!portRegex.test(port.toString())) return 'Please enter a valid port.';

        return undefined;
    };

    const validateUsername = (username?: string): string | undefined => {
        if (!username) return 'Please enter a username.';
        if (username.length < 4 || username.length > 256) return 'Username must be between 4 and 256 characters.';
        return undefined;
    };

    const validatePassword = (password?: string): string | undefined => {
        if (!password) return 'Please enter a password.';
        if (password.length < 8 || password.length > 128) return 'Password must be between 8 and 128 characters.';
        return undefined;
    };

    const validateSender = (sender?: string): string | undefined => {
        if (!sender) return 'Please enter a sender.';
        if (!emailRegex.test(sender)) return 'Please enter a valid email.';
        if (sender.length < 6 || sender.length > 254) return 'Sender must be between 6 and 254 characters.';
        return undefined;
    };

    const validateEnableStartTls = (startTlsEnabled?: boolean): string | undefined => {
        if (startTlsEnabled === undefined) return 'Please select a value.';
        return undefined;
    };

    const validateEnableSsl = (sslEnabled?: boolean): string | undefined => {
        if (sslEnabled === undefined || sslEnabled === null)  return 'Please select a value.';
        return undefined;
    };

    const validateConnectionTimeout = (connectionTimeout?: number): string | undefined => {
        if (!connectionTimeout) return 'Please enter a connection timeout.';
        if (connectionTimeout < 1 || connectionTimeout > 300) return 'Connection Timeout must be between 1 and 300 seconds.';
        return undefined;
    };

    const validateReadTimeout = (readTimeout?: number): string | undefined => {
        if (!readTimeout) return 'Please enter a read timeout.';
        if (readTimeout < 1 || readTimeout > 600) return 'Read Timeout must be between 1 and 600 seconds.';
        return undefined;
    };

    const validateWriteTimeout = (writeTimeout?: number): string | undefined => {
        if (!writeTimeout) return 'Please enter a write timeout.';
        if (writeTimeout < 1 || writeTimeout > 600) return 'Write Timeout must be between 1 and 600 seconds.';
        return undefined;
    };

    const validateIgnoreSslValidation = (ignoreSslValidation?: boolean): string | undefined => {
        if (ignoreSslValidation === undefined) return 'Please select a value.';
        return undefined;
    }

    const handleSubmit = async () => {
        if (!validate()) return;

        const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to register Email Server Settings?',
            isModal: true,
        });

        if (result) {
            setIsLoading(true);
            await registerEmailServerInfo(formData).then((response) => {
                setIsLoading(false);
                setInitialData(response.data);
                dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: 'Completed to Email Server Settings registration.',
                    isModal: true,
                });
            }).catch((error) => {
                setIsLoading(false);
                dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: `Failed to register Email Server Settings: ${error}`,
                    isModal: true,
                });
            });
        }
    };

    const handleTest = async () => {
        if (!validate()) return;
        setIsTestDialogOpen(true);
    };

    const handleTestEmailSubmit = async (email: string) => {
        setIsTestDialogOpen(false);
        setIsLoading(true);

        const testEmailFormData = {
            ...formData,
            recipient: email,
        };

        await sendTestEmail(testEmailFormData).then((response) => {
            dialogs.open(CustomDialog, {
                title: 'Notification',
                message: 'Completed test email sending.',
                isModal: true,
            },{onClose: async (result) =>  setIsLoading(false),});
            setIsLoading(false)
        }).catch((error) => {
            dialogs.open(CustomDialog, {
                title: 'Notification',
                message: `Failed to send teset email: ${error}`,
                isModal: true,
            },{onClose: async (result) =>  setIsLoading(false),});
            setIsLoading(false)
          });
    };

    useEffect(() => {
        const fetchData = async () => {
            try {
                const { data } = await getEmailServerInfo();
                if (data?.host) {
                    setFormData(data);
                    console.log(formData);
                    setInitialData(data);
                    setIsEditMode(true);
                }
                setIsLoading(false);
            } catch (err) {
                setIsLoading(false);
                navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch Email Server Settings") } });
            } 
        };

        fetchData();
    }, []);

    useEffect(() => {
        const isModified = Object.keys(formData).some(
          (key) => formData[key as keyof EmailServerFormData] !== initialData[key as keyof EmailServerFormData]
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
                <StyledSubTitle>Email Server Settings</StyledSubTitle>
                <StyledDescription>
                    <Typography variant="body1">
                        This page allows you to configure the email server settings required for sending emails.
                        You can specify the SMTP server details, authentication settings, and security options
                        such as STARTTLS and SSL.
                    </Typography>
                    <Typography variant="body1" sx={{ mt: 1 }}>
                        After setting up the email server, you can send a test email to verify that the configuration
                        is correct before saving the settings.
                    </Typography>
                    <Typography variant="body1" sx={{ mt: 1 }}>
                        If the email server uses a self-signed certificate or an untrusted SSL certificate, 
                        you can enable the "Ignore SSL Check" option to bypass SSL validation during communication.
                        However, this is <strong>not recommended for production environments.</strong>
                    </Typography>
                </StyledDescription>
                <StyledInputArea>
                    <TextField
                        fullWidth
                        label="Host *"
                        variant="outlined"
                        margin="normal"
                        value={formData.host ?? ""}
                        onChange={handleChange('host')}
                        error={!!errors.host}
                        helperText={errors.host}
                        sx={{ maxLength: 200 }}
                    />

                    <TextField
                        fullWidth
                        label="Port *"
                        variant="outlined"
                        margin="normal"
                        value={formData.port ?? ""}
                        onChange={handleChange('port')}
                        error={!!errors.port}
                        helperText={errors.port}
                        type='number'
                        slotProps={{ htmlInput: {
                            min: 1,
                            max: 65535,
                        },
                        }}
                    />

                    <TextField
                        fullWidth
                        label="User Name *"
                        variant="outlined"
                        margin="normal"
                        value={formData.username ?? ""}
                        onChange={handleChange('username')}
                        error={!!errors.username}
                        helperText={errors.username}
                        sx={{ minLength: 4, maxLength: 256 }}
                    />

                    <FormControl fullWidth margin="normal" error={!!errors.password}>
                        <InputLabel htmlFor="outlined-adornment-password">Password *</InputLabel>
                        <OutlinedInput
                            id="outlined-adornment-password"
                            type={showPassword ? 'text' : 'password'}
                            value={formData.password ?? ""}
                            onChange={handleChange('password')}
                            endAdornment={
                            <InputAdornment position="end">
                                <IconButton
                                    aria-label={showPassword ? 'hide the password' : 'display the password'}
                                    onClick={handleClickShowPassword}
                                    edge="end"
                                >
                                    {showPassword ? <VisibilityOff /> : <Visibility />}
                                </IconButton>
                            </InputAdornment>
                            }
                            label="Password"
                            inputProps={{ minLength: 8, maxLength: 128 }}
                        />
                        {errors.password && <FormHelperText>{errors.password}</FormHelperText>}
                    </FormControl>

                    <TextField
                        fullWidth
                        label="Sender *"
                        variant="outlined"
                        margin="normal"
                        value={formData.sender ?? ""}
                        onChange={handleChange('sender')}
                        error={!!errors.sender}
                        helperText={errors.sender}
                        sx={{ minLength: 6, maxLength: 254 }}
                        type='email'
                    />

                    <FormControl fullWidth variant="outlined" sx={{ mt: 2 }} error={!!errors.startTlsEnabled}>
                        <InputLabel shrink>Enable STARTTLS *</InputLabel>
                        <OutlinedInput
                            notched
                            label="Enable STARTTLS"
                            startAdornment={
                                <Switch
                                    checked={formData.startTlsEnabled ?? false}
                                    onChange={handleChange("startTlsEnabled")}
                                    color="primary"
                                    sx={{ transform: "scale(1.2)", mr: 1 }} 
                                />
                            }
                        />
                        {errors.startTlsEnabled && <FormHelperText>{errors.startTlsEnabled}</FormHelperText>}
                    </FormControl>

                    <FormControl fullWidth variant="outlined" sx={{ mt: 2 }} error={!!errors.sslEnabled}>
                        <InputLabel shrink>Enable SSL *</InputLabel>
                        <OutlinedInput
                            notched
                            label="Enable SSL"
                            startAdornment={
                                <Switch
                                    checked={formData.sslEnabled ?? false}
                                    onChange={handleChange("sslEnabled")}
                                    color="primary"
                                    sx={{ transform: "scale(1.2)", mr: 1 }} 
                                />
                            }
                        />
                        {errors.sslEnabled && <FormHelperText>{errors.sslEnabled}</FormHelperText>}
                    </FormControl>

                    <TextField
                        fullWidth
                        label="Connection Timeout (seconds) *"
                        variant="outlined"
                        margin="normal"
                        value={formData.connectionTimeout ?? ""}
                        onChange={handleChange('connectionTimeout')}
                        error={!!errors.connectionTimeout}
                        helperText={errors.connectionTimeout}
                        type='number'
                        slotProps={{ htmlInput: {
                            min: 1,
                            max: 300,
                        },
                        }}
                    />

                    <TextField
                        fullWidth
                        label="Read Timeout (seconds) *"
                        variant="outlined"
                        margin="normal"
                        value={formData.readTimeout ?? ""}
                        onChange={handleChange('readTimeout')}
                        error={!!errors.readTimeout}
                        helperText={errors.readTimeout}
                        type='number'
                        slotProps={{ htmlInput: {
                            min: 1,
                            max: 600,
                        },
                        }}
                    />

                    <TextField
                        fullWidth
                        label="Write Timeout (seconds) *"
                        variant="outlined"
                        margin="normal"
                        value={formData.writeTimeout ?? ""}
                        onChange={handleChange('writeTimeout')}
                        error={!!errors.writeTimeout}
                        helperText={errors.writeTimeout}
                        type='number'
                        slotProps={{ htmlInput: {
                            min: 1,
                            max: 600,
                        },
                        }}
                    />

                    <FormControl fullWidth variant="outlined" sx={{ mt: 2 }} error={!!errors.ignoreSslValidation}>
                        <InputLabel shrink>Turn Off SSL Check (For Testing)</InputLabel>
                        <OutlinedInput
                            notched
                            label="Turn Off SSL Check (For Testing) *"
                            startAdornment={
                                <Switch
                                    checked={formData.ignoreSslValidation ?? false}
                                    onChange={handleChange("ignoreSslValidation")}
                                    color="primary"
                                    sx={{ transform: "scale(1.2)", mr: 1 }} 
                                />
                            }
                        />
                        {errors.ignoreSslValidation && <FormHelperText>{errors.ignoreSslValidation}</FormHelperText>}
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
                        <Button 
                            variant="contained" 
                            color="primary" 
                            onClick={handleTest}>
                            Test
                        </Button>
                        
                        
                    </Box>

                    <TestEmailDialog
                        open={isTestDialogOpen}
                        onClose={() => setIsTestDialogOpen(false)}
                        onSubmit={handleTestEmailSubmit}
                    />
            </StyledInputArea>
            </StyledContainer>
        </>
    )
}

export default EmailServerSettingsPage