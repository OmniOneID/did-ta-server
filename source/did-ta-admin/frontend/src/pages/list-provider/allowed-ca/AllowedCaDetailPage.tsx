import { useDialogs } from '@toolpad/core';
import React, { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router';
import { Box, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme } from '@mui/material';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { getAllowedCaInfo } from '../../../apis/list-api';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';

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
                  navigate('/error', { state: { message: `Failed to fetch Allowed CA List: ${err}` } });
            }
        };

        fetchData();
    }, [numericId]);

    
  return (
    <>
        <FullscreenLoader open={isLoading} />
        <Box sx={{ p: 3 }}>
            <Typography variant="h4">Allowd CA List Detail Information</Typography>
            <TextField 
                fullWidth
                label="Wallet Identifier" 
                variant="standard"
                margin="normal" 
                value={formData.walletId || ''} 
                sx={{minWidth: 250}}
            />

            <Typography variant="h6" sx={{ mt: 3 }}>Allowd Ca List</Typography>
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
                <Button variant="contained" color="secondary" onClick={() => navigate('/list-settings/allowed-ca')}>
                    Back
                </Button>
                <Button variant="contained" color="primary" onClick={() => navigate('/list-settings/allowed-ca/allowed-ca-edit/' + numericId)}>
                    Edit
                </Button>
            </Box>
        </Box>
    </>
  )
}

export default AllowedCaDetailPage