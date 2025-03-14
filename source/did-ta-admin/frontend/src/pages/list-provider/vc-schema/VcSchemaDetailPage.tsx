import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router';
import { Box, Button, Popover, styled, TextField, Typography, useTheme } from '@mui/material';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { getVcSchemaInfo } from '../../../apis/list-api';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

interface VcSchemaFormData {
    title: string;
    description: string;
    issuerName: string;
    createdAt: string;
    updatedAt: string;
    vcSchema: string;
}

const VcSchemaDetailPage = (props: Props) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const theme = useTheme();

    const numericId = id ? parseInt(id, 10) : null;
    const [isLoading, setIsLoading] = useState<boolean>(true); 
    const [formData, serFormData] = useState<VcSchemaFormData>({
        title: '',
        description: '',
        issuerName: '',
        createdAt: '',
        updatedAt: '',
        vcSchema: '',
    });

    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);

    const handlePopoverOpen = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handlePopoverClose = () => {
        setAnchorEl(null);
    };

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
                const { data } = await getVcSchemaInfo(numericId);
                serFormData({
                    title: data.title,
                    description: data.description,
                    issuerName: data.issuerName,
                    createdAt: data.createdAt,
                    updatedAt: data.updatedAt,
                    vcSchema: data.vcSchema,
                });
                setIsLoading(false);
            } catch (err) {
                  console.error('Failed to fetch VC Schema information:', err);
                  setIsLoading(false);
                  navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch VC Schema") } });
            }
        };

        fetchData();
    }, [numericId]);

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
        width: 600,
        margin: 'auto',
        marginTop: theme.spacing(3),
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
            <Typography variant="h4">VC Schema Management</Typography>
            <StyledContainer>
                <StyledSubTitle>VC Schema Detail Information</StyledSubTitle>
                <StyledInputArea>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <TextField 
                            fullWidth
                            label="Title" 
                            variant="standard"
                            margin="normal" 
                            value={formData.title || ''} 
                            sx={{minWidth: 250}}
                            slotProps={{ input: { readOnly: true } }} 
                        />
                        <Button 
                            variant="outlined" 
                            size="small" 
                            onClick={handlePopoverOpen} 
                            disabled={!formData.title}
                            sx={{ 
                                height: '100%', 
                                flexShrink: 0, 
                                whiteSpace: 'nowrap', 
                                minWidth: 'auto',
                            }}
                        >
                            View VC Schema
                        </Button>
                    </Box>

                    <Popover
                        open={Boolean(anchorEl)}
                        anchorEl={anchorEl}
                        onClose={handlePopoverClose}
                        anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
                    >
                        <Box sx={{ p: 2, maxWidth: 500 }}>
                            <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                            {JSON.stringify(formData.vcSchema, null, 2)}
                            </Typography>
                        </Box>
                    </Popover>

                    <TextField 
                        fullWidth
                        label="Description" 
                        variant="standard"
                        margin="normal" 
                        value={formData.description || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth
                        label="Issuer Name" 
                        variant="standard"
                        margin="normal" 
                        value={formData.issuerName || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="Registered At" 
                        variant="standard" 
                        margin="normal" 
                        value={formData.createdAt || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    {formData.updatedAt && (
                        <TextField 
                            fullWidth 
                            label="Updated At" 
                            variant="standard" 
                            margin="normal" 
                            value={formData.updatedAt} 
                            slotProps={{ input: { readOnly: true } }} 
                        />
                    )}

                    <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
                        <Button variant="outlined" color="primary" onClick={() => navigate('/list-settings/vc-schema')}>
                            Back
                        </Button>
                        {/* <Button variant="contained" color="primary" onClick={() => navigate('/list-settings/vc-schema/vc-shema-edit/' + numericId)}>
                            Edit
                        </Button> */}
                    </Box>
                </StyledInputArea>
            </StyledContainer>
        </>
    )
}

export default VcSchemaDetailPage