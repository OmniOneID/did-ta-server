import { Box, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, styled, useTheme } from '@mui/material';
import { useDialogs } from '@toolpad/core';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { getAllowedCaInfo } from '../../../apis/list-api';
import CustomDialog from '../../../components/dialog/CustomDialog';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

interface AllowedCaFormData {
    walletId: string;
    caList: string[];
}

const AllowedCaDetailPage = (props: Props) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const theme = useTheme();

    const numericId = id ? parseInt(id, 10) : null;
    const [isLoading, setIsLoading] = useState<boolean>(true); 
    const [formData, serFormData] = useState<AllowedCaFormData>({
        walletId: '',
        caList: []
    });

    useEffect(() => {
        const fetchData = async () => {
            if (numericId === null || isNaN(numericId)) {
                await dialogs.open(CustomDialog, { 
                    title: 'Notification', 
                    message: 'Invalid Path.', 
                    isModal: true 
                },{
                    onClose: async () => navigate('/list-settings/allowed-ca', { replace: true }),
                });
                return;
            }

            setIsLoading(true);

            try {
                const { data } = await getAllowedCaInfo(numericId);
                serFormData({
                    walletId: data.walletId,
                    caList: JSON.parse(data.caList),
                });
                setIsLoading(false);
            } catch (err) {
                    console.error('Failed to fetch Allowed CA List information:', err);
                    setIsLoading(false);
                    navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch Allowed CA List") } });
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
            <Typography variant="h4">Allowed CA Management</Typography>
            <StyledContainer>
                <StyledTitle>Allowed CA Detail Information</StyledTitle>
                <TextField 
                    fullWidth
                    label="Wallet Identifier" 
                    variant="standard"
                    margin="normal" 
                    value={formData.walletId || ''} 
                    sx={{minWidth: 250}}
                />

                <Typography variant="h6" sx={{ mt: 3 }}>Allowd Ca List</Typography>

                <StyledInputArea>
                    <TableContainer component={Paper}>
                        <Table>
                            <TableHead>
                                <TableRow sx={{ backgroundColor: theme.palette.mode === "dark" ? theme.palette.background.paper : "#f5f5f5" }}>
                                    <TableCell>CA</TableCell> 
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {formData.caList?.map((ca, index) => (
                                    <TableRow key={index}>
                                        <TableCell>
                                            <TextField fullWidth size="small" value={ca} />
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                    <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
                        <Button variant="outlined" color="primary" onClick={() => navigate('/list-settings/allowed-ca')}>
                            Back
                        </Button>
                        <Button variant="outlined" color="primary" onClick={() => navigate('/list-settings/allowed-ca/allowed-ca-edit/' + numericId)}>
                            Go to Edit
                        </Button>
                    </Box>
                </StyledInputArea>
            </StyledContainer>
        </>
    )
}

export default AllowedCaDetailPage