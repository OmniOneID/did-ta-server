import { Box, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, styled, useTheme } from '@mui/material';
import { useDialogs } from '@toolpad/core';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import CustomDialog from '../../../components/dialog/CustomDialog';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../../utils/error-handler';
import { getUserInfo } from '../../../apis/user-api';

type Props = {}

interface FormData {
    did: string;
    pii: string;
    status: string;
    createdAt: string;
    updatedAt: string;
}

const UserDetailPage = (props: Props) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const theme = useTheme();

    const numericId = id ? parseInt(id, 10) : null;
    const [isLoading, setIsLoading] = useState<boolean>(true); 
    const [formData, serFormData] = useState<FormData>({
        did: '',
        pii: '',
        status: '',
        createdAt: '',
        updatedAt: ''
    });

    useEffect(() => {
        const fetchData = async () => {
            if (numericId === null || isNaN(numericId)) {
                await dialogs.open(CustomDialog, { 
                    title: 'Notification', 
                    message: 'Invalid Path.', 
                    isModal: true 
                },{
                    onClose: async () => navigate('/user-management/user-list', { replace: true }),
                });
                return;
            }

            setIsLoading(true);

            try {
                const { data } = await getUserInfo(numericId);
                serFormData({
                    did: data.did,
                    pii: data.pii,
                    status: data.status,
                    createdAt: data.createdAt,
                    updatedAt: data.updatedAt
                });
                setIsLoading(false);
            } catch (err) {
                setIsLoading(false);

                dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: formatErrorMessage(err, "Failed to fetch User information"),
                    isModal: true,
                });
            }
        };

        fetchData();
    }, [numericId]);

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
            <Typography variant="h4">User List</Typography>
            <StyledContainer>
                <StyledTitle>User Detail Information</StyledTitle>
                <StyledInputArea>
                    <TextField 
                        fullWidth 
                        label="DID" 
                        variant="standard" 
                        margin="normal" 
                        value={formData?.did || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="PII" 
                        variant="standard" 
                        margin="normal" 
                        value={formData?.pii || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="Status" 
                        variant="standard" 
                        margin="normal" 
                        value={formData?.status || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="Registered At" 
                        variant="standard" 
                        margin="normal" 
                        value={formData?.createdAt || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    {formData?.updatedAt && (
                        <TextField 
                            fullWidth 
                            label="Updated At" 
                            variant="standard" 
                            margin="normal" 
                            value={formData?.updatedAt || ''} 
                            slotProps={{ input: { readOnly: true } }} 
                        />
                    )}
                </StyledInputArea>
                <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
                    <Button variant="outlined" color="primary" onClick={() => navigate('/user-management/user-list')}>
                        Back
                    </Button>
                </Box>
            </StyledContainer>
        </>
    )
}

export default UserDetailPage