import { useDialogs } from '@toolpad/core';
import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { Box, Button, IconButton, Paper, SelectChangeEvent, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography } from '@mui/material';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import DeleteIcon from '@mui/icons-material/Delete';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import { registerAllowedCa } from '../../../apis/list-api';
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

    const [formData, setFormData] = useState<AllowedCaFormData>({
        walletId: '',
        caList: [],
    });

    const [errors, setErrors] = useState<ErrorState>({});
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (field: keyof AllowedCaFormData) => 
        (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<string>) => {
            const newValue = event.target.value;
            setFormData((prev) => ({ ...prev, [field]: newValue }));
    };

    const handleAddEndpoint = () => {
        setFormData((prev) => ({ ...prev, caList: [...prev.caList, ''] }));
    };

    const handleCaChange = (index: number, event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const newCaList = [...formData.caList];
        newCaList[index] = event.target.value;
        setFormData((prev) => ({ ...prev, caList: newCaList }));
    };
    
    const handleRemoveCaList = (index: number) => {
        const newCaList = [...formData.caList];
        newCaList.splice(index, 1);
        setFormData((prev) => ({ ...prev, caList: newCaList }));
    };

    const handleReset = () => {
        setErrors({});
        setIsButtonDisabled(true);
        setFormData({ caList: [], walletId: ''});
    };

    const validate = () => {
        let tempErrors: ErrorState = {};
        tempErrors.walletId = validateWalletId(formData.walletId);

        if (formData.caList.length === 0) {
            tempErrors.errorCaListMessage = "At least one caList is required.";
        } else {
            tempErrors.caList = formData.caList.map(validateItem).map(err => err.endpoint).filter(Boolean) as string[];
            if (tempErrors.caList.length === 0) tempErrors.caList = undefined;
        }

        setErrors(tempErrors);
        return Object.values(tempErrors).every((error) => !error);
    }

    const validateItem = (item: string): { endpoint?: string } => {
        let itemErrors: { endpoint?: string } = {};
    
        if (!item.trim()) itemErrors.endpoint = "Ca is required.";
    
        return itemErrors;
    };

    const validateWalletId = (walletId?: string): string | undefined => {
        if (!walletId) return 'Please enter a wallet ID.';
        if (walletId.length < 3 || walletId.length > 50) return 'Wallet ID must be between 3 and 50 characters.';
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
                    message: `Failed to register Ca List: ${error}`,
                    isModal: true,
                });
            });
        }
    };

    useEffect(() => {
        const isModified = Object.values(formData).some((value) => {
            if (Array.isArray(value)) return value.length > 0;
            return value !== '' && value !== undefined;
        });
        setIsButtonDisabled(!isModified);
    }, [formData]);

    return (
        <>
            <FullscreenLoader open={isLoading} />
            <Box sx={{ p: 3 }}>
                <Typography variant="h4">Allowd CA List Registration</Typography>
                <Box sx={{ maxWidth: 500, margin: 'auto', mt: 2, p: 3, border: '1px solid #ccc', borderRadius: 2 }}>
                    <TextField 
                        fullWidth
                        label="Wallet Identifier" 
                        variant="outlined"
                        margin="normal" 
                        size="small"
                        value={formData.walletId} 
                        onChange={handleChange('walletId')} 
                        error={!!errors.walletId} 
                        helperText={errors.walletId} 
                    />

                    <Typography variant="h6" sx={{ mt: 3 }}>Allowd Ca List</Typography>
                    {errors.errorCaListMessage && (
                        <Typography color="error" variant="caption" sx={{ mt: 1, display: "block" }}>{errors.errorCaListMessage}</Typography>
                    )}
                    <Button variant="contained" startIcon={<AddCircleOutlineIcon />} sx={{ mt: 2, mb: 2 }} onClick={handleAddEndpoint}>
                        Add Endpoint
                    </Button>

                    <TableContainer component={Paper}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>API Address</TableCell>
                                    <TableCell>Delete</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {formData.caList.map((ca, index) => (
                                    <TableRow key={index}>
                                        <TableCell>
                                            <TextField fullWidth size="small" value={ca} onChange={(event) => handleCaChange(index, event)} error={!!errors.caList?.[index]} helperText={errors.caList?.[index]} />
                                        </TableCell>
                                        <TableCell>
                                            <IconButton onClick={() => handleRemoveCaList(index)} color="error">
                                                <DeleteIcon />
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>

                    <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
                        <Button variant="contained" color="secondary" onClick={() => navigate('/vp-policy-management/service-management')}>
                            Back
                        </Button>
                        <Button variant="contained" color="secondary" onClick={handleReset}>Reset</Button>
                        <Button variant="contained" color="primary" onClick={handleSubmit} disabled={isButtonDisabled}>Register</Button>
                    </Box>
                </Box>
            </Box>

        </>
    )
}

export default AllowedCaRegistrationPage