import { Box, TextField, Typography, styled } from '@mui/material';
import React, { useEffect, useMemo, useState } from 'react'
import { sha256Hash } from '../../../utils/sha256-hash';
import { validateTaSecret } from '../../../apis/ta-api';
import { useDialogs } from '@toolpad/core';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../../utils/error-handler';

interface Props {
    step: number;
    onRegister: (step: number, validate: () => boolean, afterValidate?: () => Promise<void>) => void;
    setIsLoading: (loading: boolean) => void;
}
  
interface formData {
    password: string;
    confirmPassword: string;
}

interface ErrorState {
    password?: string;
    confirmPassword?: string;
}
  
const Step1TaPassword: React.FC<Props> = ({ step, onRegister, setIsLoading }) => {
    const [formData, setFormData] = useState<formData>({ password: '', confirmPassword: '' });
    const [errors, setErrors] = useState<ErrorState>({});
    const dialogs = useDialogs();
    
    const handleChange = (field: keyof formData) => (event: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = event.target.value;
        setFormData((prev) => ({ ...prev, [field]: newValue }));
    };

    const validate = () => {
        let tempErrors: ErrorState = {};

        tempErrors.password = validatePassword(formData.password);

        if (!formData.confirmPassword.trim()) {
            tempErrors.confirmPassword = "Please confirm TA password.";
        } else if (formData.password !== formData.confirmPassword) {
            tempErrors.confirmPassword = "Passwords do not match.";
        }

        setErrors(tempErrors);
        return Object.values(tempErrors).every((error) => !error);
    };

    const validatePassword = (password?: string): string | undefined => {
        if (!password?.trim()) return 'Please enter a password.';
        if (password.length > 64) return 'Password must be less than 64 characters.';
        return undefined;
    };

    const afterValidate = async () => {
        const hashedPassword = await sha256Hash(formData.password);

        const requestBody = {
            secret: hashedPassword
        };

        setIsLoading(true);
        await validateTaSecret(requestBody).then((response) => {
        }).catch((error) => {
            setIsLoading(false);
            dialogs.open(CustomDialog, {
                title: 'Notification',
                message: formatErrorMessage(error, `Failed to validate TA credential`),
                isModal: true,
            });
            throw error;
        });
    };

    useEffect(() => {
        onRegister(step, validate, afterValidate);
    }, [formData]);

    const StyledDescription = useMemo(() => styled(Box)(({ theme }) => ({
        maxWidth: 600, 
        marginTop: theme.spacing(1),
        padding: theme.spacing(0),
    })), []);
      
    const StyledInputArea = useMemo(() => styled(Box)(({ theme }) => ({
        marginTop: theme.spacing(2),
    })), []);
    
    return (
        <>
            <Typography variant="h6" gutterBottom>
                Step 1 – Enter TA Password
            </Typography>
            <StyledDescription>
                <Typography variant="body1">
                    This password will be required in the final step when issuing the Certificate VC.
                </Typography>
                <Typography variant="body1" sx={{ mt: 1 }}>
                    It is recorded in the Trust Agent server configuration file.
                </Typography>
            </StyledDescription>

            <StyledInputArea>
                <TextField
                    fullWidth
                    label="Password *"
                    type="password"
                    variant="outlined"
                    margin="normal"
                    value={formData.password}
                    onChange={handleChange('password')}
                    error={!!errors.password}
                    helperText={errors.password}
                    sx={{ minLength: 3, maxLength: 64 }}
                    slotProps={{ htmlInput: {maxLength: 64,},}}
                />

                <TextField
                    fullWidth
                    label="Confirm Password *"
                    type="password"
                    variant="outlined"
                    margin="normal"
                    value={formData.confirmPassword}
                    onChange={handleChange('confirmPassword')}
                    error={!!errors.confirmPassword}
                    helperText={errors.confirmPassword}
                    sx={{ minLength: 3, maxLength: 64 }}
                    slotProps={{ htmlInput: {maxLength: 64,},}}
                />
            </StyledInputArea>
        </>
    )
}

export default Step1TaPassword