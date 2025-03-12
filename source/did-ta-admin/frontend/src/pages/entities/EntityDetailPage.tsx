import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router';
import { getEntityInfo } from '../../apis/entity-api';
import { CircularProgress, Box, Typography, TextField, Button, Popover, useTheme, useMediaQuery } from '@mui/material';
import CustomDialog from '../../components/dialog/CustomDialog';
import { useDialogs } from '@toolpad/core/useDialogs';
import { formatErrorMessage } from '../../utils/error-handler';

const EntityDetailPage = () => {
    const { entityId } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();

    const numericEntityId = entityId ? parseInt(entityId, 10) : null;
    const [isLoading, setIsLoading] = useState<boolean>(true); 
    const [entityData, setEntityData] = useState<any>(null); 

    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);

    const theme = useTheme();
    const isSmallScreen = useMediaQuery(theme.breakpoints.down('sm'));

    useEffect(() => {
        const fetchData = async () => {
            if (numericEntityId === null || isNaN(numericEntityId)) {
                await dialogs.open(CustomDialog, { 
                    title: 'Notification', 
                    message: 'Invalid Path.', 
                    isModal: true 
                },{
                    onClose: async (result) =>  navigate('/entities/entity-management', { replace: true }),
                });
                return;
            }

            setIsLoading(true);

            try {
                const { data } = await getEntityInfo(numericEntityId);
                setEntityData(data);
                setIsLoading(false);
            } catch (err) {
                console.error('Failed to fetch Entity information:', err);
                setIsLoading(false);
                navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch Entity information") } });
            } 
        };
        fetchData();
    }, []);

    const handlePopoverOpen = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handlePopoverClose = () => {
        setAnchorEl(null);
    };

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4">Entity Detail Information</Typography>
    
            {isLoading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px' }}>
                    <CircularProgress />
                </Box>
            ) : (
                <Box sx={{ maxWidth: 400, margin: 'auto', mt: 1, p: 3, border: '1px solid #ccc', borderRadius: 2 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <TextField 
                            fullWidth 
                            label="DID" 
                            variant="standard" 
                            margin="normal" 
                            value={entityData.did}
                            slotProps={{ input: { readOnly: true } }} 
                        />
                        <Button 
                            variant="outlined" 
                            size="small" 
                            onClick={handlePopoverOpen} 
                            disabled={entityData.didDocument ? false : true}
                            sx={{
                                height: '100%', 
                                flexShrink: 0, 
                                whiteSpace: 'nowrap', 
                                minWidth: 'auto',
                            }}
                        >
                            View DID Document
                        </Button>
                    </Box>

                    <Popover
                        open={Boolean(anchorEl)}
                        anchorEl={anchorEl}
                        onClose={handlePopoverClose}
                        anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
                        slotProps={{
                            paper: {
                                sx: {
                                p: 2,
                                maxWidth: isSmallScreen ? '90vw' : 500,
                                width: '100%',
                                },
                            },
                        }}
                    >
                        <Box sx={{ p: 2, maxWidth: 500 }}>
                            <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                            {JSON.stringify(entityData.didDocument, null, 2)}
                            </Typography>
                        </Box>
                    </Popover>

                    <TextField 
                        fullWidth 
                        label="Name" 
                        variant="standard" 
                        margin="normal" 
                        value={entityData.name} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="Role" 
                        variant="standard" 
                        margin="normal" 
                        value={entityData.role} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="Status" 
                        variant="standard" 
                        margin="normal" 
                        value={entityData.status} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="Server URL" 
                        variant="standard" 
                        margin="normal" 
                        value={entityData.serverUrl} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="Registered At" 
                        variant="standard" 
                        margin="normal" 
                        value={entityData.createdAt} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    {entityData.updatedAt && (
                        <TextField 
                            fullWidth 
                            label="Updated At" 
                            variant="standard" 
                            margin="normal" 
                            value={entityData.updatedAt} 
                            slotProps={{ input: { readOnly: true } }} 
                        />
                    )}
                </Box>
            )}

            <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
                <Button variant="contained" color="primary" onClick={() => navigate('/entities/entity-management')}>
                    Back
                </Button>
            </Box>

        </Box>
    );
    
};

export default EntityDetailPage;
