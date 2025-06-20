import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { Box, Button, IconButton, Paper, SelectChangeEvent, styled, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme } from '@mui/material';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import DeleteIcon from '@mui/icons-material/Delete';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import { registerAllowedCa, verifyWalletIdUnique } from '../../../apis/list-api';
import CustomDialog from '../../../components/dialog/CustomDialog';

type Props = {}

interface AllowedCaFormData {
    walletId: string;
    caList: string[];
}

interface ErrorState {
    walletId?: string;
    caList?: string[];
    errorCaListMessage?: string;
}


const AllowedCaRegistrationPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const theme = useTheme();

    const [formData, setFormData] = useState<AllowedCaFormData>({
        walletId: '',
        caList: [],
    });

    const [errors, setErrors] = useState<ErrorState>({});
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [isWalletIsValid, setIsWalletIdValid] = useState(false);
    const [walletIdCheckMessage, setWalletIdCheckMessage] = useState<string>('');
    const [walletIdCheckStatus, setWalletIdCheckStatus] = useState<'success' | 'error' | ''>('');

    const handleChange = (field: keyof AllowedCaFormData) => 
        (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<string>) => {
            const newValue = event.target.value;
            setFormData((prev) => ({ ...prev, [field]: newValue }));

            if (field === 'walletId') {
                setIsWalletIdValid(false);
                setErrors((prev) => ({ ...prev, walletId: undefined }));
                setWalletIdCheckMessage('');
                setWalletIdCheckStatus('');
            }
    };

    const handleCa = () => {
        setFormData((prev) => ({ ...prev, caList: [...prev.caList, ''] }));
    };

    const handleCaChange = (index: number, event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const newCaList = [...formData.caList];
        newCaList[index] = event.target.value;
        setFormData((prev) => ({ ...prev, caList: newCaList }));
    };
    
    const handleRemoveCa = (index: number) => {
        const newCaList = [...formData.caList];
        newCaList.splice(index, 1);
        setFormData((prev) => ({ ...prev, caList: newCaList }));
    };

    const handleReset = () => {
        setErrors({});
        setIsButtonDisabled(true);
        setFormData({ caList: [], walletId: ''});
        setIsWalletIdValid(false);
        setWalletIdCheckMessage('');
        setWalletIdCheckStatus('');
    };

    const validate = () => {
        let tempErrors: ErrorState = {};
        tempErrors.walletId = validateWalletId(formData.walletId);
    
        if (formData.caList.length === 0) {
            tempErrors.errorCaListMessage = "At least one CA is required.";
        } else {
            const seen = new Set<string>();
            const duplicateIndices: number[] = [];
    
            formData.caList.forEach((value, index) => {
                if (seen.has(value) && value.trim() !== "") {
                    duplicateIndices.push(index);
                }
                seen.add(value);
            });
    
            const caErrors = formData.caList.map((item, index) => {
                if (!item.trim()) return "Ca is required.";
                if (duplicateIndices.includes(index)) return "Duplicate CA is not allowed.";
                return "";
            });
    
            tempErrors.caList = caErrors.every(err => err === "") ? undefined : caErrors;
        }
    
        setErrors(tempErrors);
        return Object.values(tempErrors).every((error) => !error);
    };
    
    const validateWalletId = (walletId?: string): string | undefined => {
        if (!walletId) return 'Please enter a wallet Identifier.';
        if (walletId.length < 3 || walletId.length > 50) return 'Wallet Identifier must be between 3 and 50 characters.';
        if (!isWalletIsValid) return 'Please check for duplicate wallet Identifier.';
        return undefined;
    };

    const handleSubmit = async () => {
        if (!validate()) return;

        const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to register Allowed Ca List?',
            isModal: true,
        });

        if (result) {
            setIsLoading(true);

            let requestObject = {
                walletId: formData.walletId,
                caList: JSON.stringify(formData.caList),
            }

            await registerAllowedCa(requestObject).then((response) => {
                setIsLoading(false);
                dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: 'Allowed Ca List registration completed.',
                    isModal: true,
                },{
                    onClose: async (result) =>  navigate('/list-settings/allowed-ca'),
                });
    
            }).catch((error) => {
                setIsLoading(false);
                dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: `Failed to register Allowed Ca List: ${error}`,
                    isModal: true,
                });
            });
        }
    };

    const handleCheckDuplicateWalletID = () => {
         verifyWalletIdUnique(formData.walletId as string)
        .then((response) => {
            if (response.data.unique === false) {
                setErrors((prev) => ({ ...prev, walletId: 'WalletId already exists.' }));
                setIsWalletIdValid(false);
                setWalletIdCheckMessage('This wallet identifier is already in use. Please choose a different one.');
                setWalletIdCheckStatus('error');
            } else {        
                setIsWalletIdValid(true);
                setErrors((prev) => ({ ...prev, walletId: undefined }));
                setWalletIdCheckMessage('This wallet identifier is available for use.');
                setWalletIdCheckStatus('success');
            }
        })
        .catch((error) => {
            setIsWalletIdValid(false);
            setWalletIdCheckMessage('Failed to check wallet identifier availability. Please try again.');
            setWalletIdCheckStatus('error');
        });
    };
    
    const handleCancel = async () => {
        const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to cancel Allowed Ca List registration?',
            isModal: true,
        });

        if (result) {
            navigate('/list-settings/allowed-ca');
        }
    };

    useEffect(() => {
        const isModified = Object.values(formData).some((value) => {
            if (Array.isArray(value)) return value.length > 0;
            return value !== '' && value !== undefined;
        });
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
    
    const StyledTitle = useMemo(() => styled(Typography)({
        textAlign: 'left',
        fontSize: '24px',
        fontWeight: 700,
    }), []);
    
    const StyledInputArea = useMemo(() => styled(Box)(({ theme }) => ({
        marginTop: theme.spacing(2),
    })), []);

    return (
        <>
            <FullscreenLoader open={isLoading} />
            <Typography variant="h4">Allowed CA Management</Typography>
            <StyledContainer>
                <StyledTitle>Allowd CA Registration</StyledTitle>
                <StyledInputArea>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <TextField 
                            fullWidth
                            label="Wallet Identifier *" 
                            variant="outlined"
                            margin="normal" 
                            size="small"
                            value={formData.walletId} 
                            onChange={handleChange('walletId')} 
                            error={!!errors.walletId} 
                            helperText={errors.walletId || walletIdCheckMessage}
                            sx={{
                                minWidth: 250,
                                '& .MuiFormHelperText-root': {
                                    color: walletIdCheckStatus === 'success' ? 'green' : 
                                           walletIdCheckStatus === 'error' ? 'red' : 'inherit',
                                    fontWeight: walletIdCheckStatus ? 500 : 'inherit'
                                }
                            }}
                        />
                        <Button 
                            variant="contained" 
                            onClick={handleCheckDuplicateWalletID}
                            disabled={!formData.walletId}
                            sx={{ 
                                minWidth: 150,  
                                whiteSpace: 'nowrap', 
                                textTransform: 'none' 
                            }}
                        >
                            Check Availability
                        </Button>
                    </Box>
                

                    <Typography variant="h6" sx={{ mt: 3 }}>Allowd Ca List</Typography>
                    {errors.errorCaListMessage && (
                        <Typography color="error" variant="caption" sx={{ mt: 1, display: "block" }}>{errors.errorCaListMessage}</Typography>
                    )}
                    <Button variant="contained" startIcon={<AddCircleOutlineIcon />} sx={{ mt: 2, mb: 2 }} onClick={handleCa}>
                        Add CA
                    </Button>

                    <TableContainer component={Paper}>
                        <Table>
                            <TableHead>
                                <TableRow sx={{ backgroundColor: theme.palette.mode === "dark" ? theme.palette.background.paper : "#f5f5f5" }}>
                                    <TableCell sx={{ width: '80%' }}>CA</TableCell>
                                    <TableCell>Delete</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {formData.caList.map((ca, index) => (
                                    <TableRow key={index}>
                                        <TableCell sx={{ verticalAlign: 'top', width: '80%' }}>
                                            <TextField 
                                                fullWidth 
                                                label="CA *"
                                                size="small" 
                                                value={ca} 
                                                onChange={(event) => handleCaChange(index, event)} 
                                                error={!!errors.caList?.[index]} 
                                                helperText={errors.caList && errors.caList[index] ? errors.caList[index] : ""}
                                            />
                                        </TableCell>
                                        <TableCell sx={{ verticalAlign: 'top', width: '20%', textAlign: 'center' }}>
                                            <IconButton onClick={() => handleRemoveCa(index)} sx={{ color: '#FF8400' }}>
                                                <DeleteIcon sx={{ color: '#FF8400' }} />
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>

                    <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
                        <Button variant="contained" color="primary" onClick={handleSubmit} disabled={isButtonDisabled}>Register</Button>
                        <Button variant="contained" color="secondary" onClick={handleReset}>Reset</Button>
                        <Button variant="outlined" color="secondary" onClick={handleCancel}>
                            Cancel
                        </Button>
                    </Box>
                </StyledInputArea>
            </StyledContainer>

        </>
    )
}

export default AllowedCaRegistrationPage