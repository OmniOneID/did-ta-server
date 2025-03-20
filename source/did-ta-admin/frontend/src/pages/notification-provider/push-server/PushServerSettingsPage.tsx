import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useRef, useState } from 'react'
import { useNavigate } from 'react-router';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { Box, Button, FormControl, FormHelperText, styled, Typography } from '@mui/material';
import { getNotificationServerStatus, registerPushServerInfo } from '../../../apis/noti-api';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

interface PushServerFormData {
   push?: string;
   pushFileName?: string;
}

interface ErrorState {
    push?: string;
}

const PushServerSettingsPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();

    const [isLoading, setIsLoading] = useState(true);
    const [formData, setFormData] = useState<PushServerFormData>({ push: '' });
    const [errors, setErrors] = useState<ErrorState>({});
    const [isEditMode, setIsEditMode] = useState(false);
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);
    const [fileName, setFileName] = useState<string>('');
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [isPushConfigured, setIsPushConfigured] = useState(false);

    const fileInputRef = useRef<HTMLInputElement | null>(null);

    const handleChange = (field: keyof PushServerFormData) => (event: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = event.target.value;
        setFormData((prev) => ({ ...prev, [field]: newValue }));
    };

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];

        if (file) {
            if (!file.name.endsWith('.json')) {
                setErrors((prev) => ({ ...prev, push: 'Only .json files are allowed.' }));
                return;
            }

            setErrors((prev) => ({ ...prev, push: undefined })); 
            setSelectedFile(file);
            setFileName(file.name);
            setFormData((prev) => ({ ...prev, pushFileName: file.name })); 

            const reader = new FileReader();
            reader.onload = (e) => {
                setFormData((prev) => ({ ...prev, push: e.target?.result as string }));
            };
            reader.readAsText(file);
        }
    };

    const handleReset = () => {
        setFormData({
            push: '',
        });
        setErrors({});
        setIsButtonDisabled(true);
        setSelectedFile(null);
        setFileName('');
    };


    const handleSubmit = async () => {
        if (!validate()) return;

        const formDataObj = new FormData();
        formDataObj.append('push', selectedFile as File);

        const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to register Push Server Setting?',
            isModal: true,
        });

        if (result) {
            setIsLoading(true);
            await registerPushServerInfo(formDataObj).then(() => {
                setIsLoading(false);
                setIsPushConfigured(true);
                setFileName('');
                setIsButtonDisabled(true);
                setSelectedFile(null);
                setFormData({
                    push: '',
                });

                if (fileInputRef.current) {
                    fileInputRef.current.value = '';
                }
                
                dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: 'Completed to Push Server Settings registration.',
                    isModal: true,
                });
                
            }).catch((error) => {
                setIsLoading(false);
                dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: `Failed to register Push Server Settings: ${error}`,
                    isModal: true,
                });
            });
        }
    };

    const validate = () => {
        let tempErrors: ErrorState = {};

        if (!selectedFile) tempErrors.push = 'Please select a FCM Setting file.';

        setErrors(tempErrors);
        return Object.values(tempErrors).every((error) => !error);
    };


    useEffect(() => {
        try {
            const fetchData = async () => {
                const { data } = await getNotificationServerStatus();
                if (data) {
                    setIsLoading(false);
                    setIsPushConfigured(data.pushConfigured);
                }
            };

            fetchData();
        } catch (err) {
            setIsLoading(false);
            navigate('/error', { state: { message: formatErrorMessage(err, "Failed to Find Push Server Settings") } });
        }
    }, []);

    React.useEffect(() => {
        const isModified = Object.values(formData).some((value) => value !== '');
        setIsButtonDisabled(!isModified);
    }, [formData]);

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
        marginTop: theme.spacing(1),
    })), []);

    return (
        <>
            <FullscreenLoader open={isLoading} />
            <StyledContainer>
                <StyledSubTitle>Push Server Settings</StyledSubTitle>
                    <StyledDescription>
                        {!isLoading && !isPushConfigured && (
                            <Box sx={{
                                display: 'flex',
                                alignItems: 'center',
                                backgroundColor: '#ffebee',
                                border: '1px solid red',
                                color: 'red',
                                borderRadius: 2,
                                p: 2,
                            }}>
                                <WarningAmberIcon sx={{ fontSize: 22, mr: 1 }} />
                                <Typography variant="body2">
                                    Push server configuration is incomplete. Please complete the push server setup.
                                </Typography>
                            </Box>
                        )}

                        {!isLoading && isPushConfigured && (
                            <Box sx={{
                                display: 'flex',
                                alignItems: 'center',
                                backgroundColor: '#e8f5e9',
                                border: '1px solid green',
                                color: 'green',
                                borderRadius: 2,
                                p: 2,
                            }}>
                                <CheckCircleIcon sx={{ fontSize: 22, mr: 1 }} />
                                <Typography variant="body2">
                                    Push server settings have been successfully registered.
                                </Typography>
                            </Box>
                        )}
                    </StyledDescription>

                    <StyledInputArea>
                        <FormControl fullWidth margin="normal" error={!!errors.push}>
                            <Box sx={{
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'left',
                                border: '1px solid #ccc',
                                borderRadius: 2,
                                p: 2,
                            }}>
                                <Typography variant="body1" sx={{ mr: 5 }}>FCM Setting File *</Typography>

                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                                    <Button variant="contained" component="label">
                                        Select File
                                        <input type="file" hidden accept=".json" onChange={handleFileChange} ref={fileInputRef} />
                                    </Button>

                                    {formData.pushFileName && (
                                        <Typography variant="body2" sx={{
                                            color: 'red',
                                            whiteSpace: 'nowrap',
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis',
                                            maxWidth: 150
                                        }}>
                                            {formData.pushFileName}
                                        </Typography>
                                    )}
                                </Box>
                            </Box>
                            {errors.push && <FormHelperText>{errors.push}</FormHelperText>}
                        </FormControl>

                        <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 2 }}>
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

export default PushServerSettingsPage