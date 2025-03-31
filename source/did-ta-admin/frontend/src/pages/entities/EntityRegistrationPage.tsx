import { Button, FormControl, FormHelperText, InputLabel, MenuItem, Select, SelectChangeEvent, styled, TextField, Typography } from '@mui/material';
import Box from '@mui/material/Box';
import { useDialogs } from '@toolpad/core/useDialogs';
import React, { useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import { registerEntity, verifyEntityNameUnique } from '../../apis/entity-api';
import { verifyServerUrl } from '../../apis/server-api';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../components/dialog/CustomDialog';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { roles } from '../../constants/roles';
import { englishRegex, ipRegex, urlRegex } from '../../utils/regex';

type Props = {}

interface EntityFormData {
    didDoc: string;
    name?: string;
    role?: string;
    serverUrl?: string;
    didFileName?: string;
}

interface ErrorState {
    didDoc?: string;
    name?: string;
    role?: string;
    serverUrl?: string;
}

const EntityRegistrationPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();

    const [formData, setFormData] = useState<EntityFormData>({
        didDoc: '',
        name: '',
        role: '',
        serverUrl: '',
        didFileName: '',
    });

    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [fileName, setFileName] = useState<string>('');

    const [errors, setErrors] = useState<ErrorState>({});
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);

    const [isNameValid, setIsNameValid] = useState(false);
    const [isServerValid, setIsServerValid] = useState(false);

    const [isLoading, setIsLoading] = useState(false);

    React.useEffect(() => {
        const isModified = Object.values(formData).some((value) => value !== '');
        setIsButtonDisabled(!isModified);
      }, [formData]);

    const handleChange = (field: keyof EntityFormData) => 
    (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<string>) => {
        const newValue = event.target.value as string;
        setFormData((prev) => ({ ...prev, [field]: newValue }));

        if (field === 'name') {
            setIsNameValid(false); 
            setErrors((prev) => ({ ...prev, name: undefined })); 
        }

        if (field === 'serverUrl') {
            setIsServerValid(false); 
            setErrors((prev) => ({ ...prev, serverUrl: undefined }));
        }
    };

    const handleCheckDuplicateName = () => {
        verifyEntityNameUnique(formData.name as string)
            .then((response) => {
                if (response.data.unique === false) {
                    setErrors((prev) => ({ ...prev, name: 'Name already exists.' }));
                    setIsNameValid(false);
                } else {        
                    setIsNameValid(true);
                    setErrors((prev) => ({ ...prev, name: undefined }));
                }
            });
    };

    const handleTestServerConnection = async () => {
        if (!formData.serverUrl) {
            setErrors((prev) => ({ ...prev, serverUrl: 'Please enter the server URL.' }));
            setIsServerValid(false);
            return;
        }
 
        if (!urlRegex.test(formData.serverUrl) && !ipRegex.test(formData.serverUrl)) {
            setErrors((prev) => ({ ...prev, serverUrl: 'Please enter a valid URL.' }));
            setIsServerValid(false);
            return;
        }

        let baseUrl;
        try {
            const url = new URL(formData.serverUrl);
            baseUrl = `${url.protocol}//${url.host}`;
        } catch (error) {
            setErrors((prev) => ({ ...prev, serverUrl: 'Invalid URL format.' }));
            setIsServerValid(false);
            return;
        }
    
        try {
            const response = await verifyServerUrl({ serverUrl: baseUrl });
            if (response.data.isAvailable === false) {
                setErrors((prev) => ({ ...prev, serverUrl: 'Test Connection failed.' }));
                setIsServerValid(false);
            } else {
                setIsServerValid(true);
                setErrors((prev) => ({ ...prev, serverUrl: undefined }));
            }
        } catch (error) {
            setErrors((prev) => ({ ...prev, serverUrl: 'Error occurred while testing connection.' }));
            setIsServerValid(false);
        }
    };

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];

        if (file) {
            if (!file.name.endsWith('.did')) {
                setErrors((prev) => ({ ...prev, didDoc: 'Only .did files are allowed.' }));
                return;
            }

            setErrors((prev) => ({ ...prev, didDoc: undefined })); 
            setSelectedFile(file);
            setFileName(file.name);
            setFormData((prev) => ({ ...prev, didFileName: file.name })); 

            const reader = new FileReader();
            reader.onload = (e) => {
                setFormData((prev) => ({ ...prev, didDoc: e.target?.result as string }));
            };
            reader.readAsText(file);
        }
    };

    const validate = () => {
        let tempErrors: ErrorState = {};
        
        // Validate File
        if (!selectedFile) tempErrors.didDoc = 'Please select a DID document file.';
        
        // Validate Name
        tempErrors.name = validateName(formData.name);

        // Validate Role
        if (!formData.role) tempErrors.role = 'Please select a role.';
    
        // Validate Server URL
        tempErrors.serverUrl = validateServerUrl(formData.serverUrl);
    
        setErrors(tempErrors);
        return Object.values(tempErrors).every((error) => !error);
    };

    const validateName = (name?: string): string | undefined => {
        if (!name) return 'Please enter a name.';
        if (name.length < 3 || name.length > 20) return 'Name must be between 3 and 20 characters.';
        if (!englishRegex.test(name)) return 'Name must be in English.';
        if (!isNameValid) return 'Please check for duplicate names.';
        return undefined;
    };

    const validateServerUrl = (serverUrl?: string): string | undefined => {
        if (!serverUrl) return 'Please enter the server URL.';
        if (!urlRegex.test(serverUrl) && !ipRegex.test(serverUrl)) return 'Please enter a valid URL.';
        if (serverUrl.length > 200) return 'URL must be less than 200 characters.';
        if (!isServerValid) return 'Please test the server connection.';
        return undefined;
    };

    const handleSubmit = async () => {
        if (!validate()) return;
        
        const formDataObj = new FormData();
        formDataObj.append('didDoc', selectedFile as File);
        formDataObj.append('name', formData.name || '');
        formDataObj.append('role', formData.role || '');
        formDataObj.append('serverUrl', formData.serverUrl || '');
        formDataObj.append('certificateUrl', formData.serverUrl + '/api/v1/certificate-vc');
        
        const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to register Entity?',
            isModal: true,
        });

        if (result) {
            setIsLoading(true);
            try {
                await registerEntity(formDataObj);
                setIsLoading(false);
                await dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: 'Completed entity registration.',
                    isModal: true,
                },{
                    onClose: async (result) =>  navigate('/entities/entity-management'),
                });
            } catch (error) {
                await dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: `Failed to register entity: ${error}`,
                    isModal: true,
                });
            } finally {
                setIsLoading(false);
            }
        }
    };

    const handleCancel = async () => {
        const result = await dialogs.open(CustomConfirmDialog, {
          title: 'Confirmation',
          message: 'Are you sure you want to cancel entity registration?',
          isModal: true,
        });
    
        if (result) {
          navigate('/entities/entity-management');
        }
    };

    const handleReset = () => {
        setFormData({
            didDoc: '',
            name: '',
            role: '',
            serverUrl: '',
        });
        setErrors({});
        setIsButtonDisabled(true);
        setIsNameValid(false);
        setIsServerValid(false);
        setSelectedFile(null);
        setFileName('');
    };

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
        width: 500,
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
            <Typography variant="h4">Entity Management</Typography>
            <StyledContainer>
                <StyledSubTitle>Entity Registration</StyledSubTitle>
                <StyledDescription>
                    <Typography variant="body1">
                        Register an entity's DID Document on the blockchain. Once registration is complete, 
                        the entity admin must submit a request for a joining certificate issuance.
                    </Typography>
                    <Typography variant="body1" sx={{ mt: 1 }}>
                        Deleting a registered entity is currently not supported. This feature will be available in a future update.
                    </Typography>
                </StyledDescription>
                <StyledInputArea>         
                    <FormControl fullWidth margin="normal" error={!!errors.didDoc}>
                        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'left', mb: 1 }}>
                            <Typography variant="body1" sx={{ mr: 5 }}>DID Document *</Typography>

                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, minWidth: 200 }}>
                                <Button variant="outlined" component="label">
                                    File
                                    <input type="file" hidden accept=".did" onChange={handleFileChange} />
                                </Button>

                                {formData.didFileName && (
                                    <Typography variant="body2" sx={{ color: 'red', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', maxWidth: 200 }}>
                                        {formData.didFileName}
                                    </Typography>
                                )}
                            </Box>
                        </Box>
                        {errors.didDoc && <FormHelperText>{errors.didDoc}</FormHelperText>}
                    </FormControl>

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <TextField
                            fullWidth
                            label="Name *"
                            variant="outlined"
                            margin="normal"
                            value={formData.name}
                            onChange={handleChange('name')}
                            error={!!errors.name}
                            helperText={errors.name}
                            sx={{minWidth: 250}}
                            slotProps={{ htmlInput: {
                                    minLength: 3,
                                    maxLength: 20,
                                    },
                                }
                            }
                        />
                        <Button 
                            variant="outlined" 
                            onClick={handleCheckDuplicateName}
                            disabled={!formData.name}
                            sx={{ 
                                minWidth: 150,  
                                whiteSpace: 'nowrap', 
                                textTransform: 'none' 
                            }}
                        >
                            Check Availability
                        </Button>
                    </Box>

                    <FormControl fullWidth margin="normal" error={!!errors.role}>
                        <InputLabel>Role *</InputLabel>
                        <Select value={formData.role} onChange={handleChange('role')} label="Role">
                        {roles.map((role) => (
                            <MenuItem key={role.value} value={role.value}>
                                {role.label}
                            </MenuItem>
                        ))}
                        </Select>
                        {errors.role && <FormHelperText>{errors.role}</FormHelperText>}
                    </FormControl>

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <TextField
                            fullWidth
                            label="URL *"
                            variant="outlined"
                            margin="normal"
                            value={formData.serverUrl}
                            onChange={handleChange('serverUrl')}
                            error={!!errors.serverUrl}
                            helperText={errors.serverUrl}
                            sx={{minWidth: 250}}
                            slotProps={{ htmlInput: {
                                    maxLength: 200,
                                    },
                                }
                            }
                        />
                        <Button 
                            variant="outlined" 
                            onClick={handleTestServerConnection} 
                            disabled={!formData.serverUrl}
                            sx={{ 
                                minWidth: 150,  
                                whiteSpace: 'nowrap',  
                                textTransform: 'none'
                            }}
                        >
                            Test Connection
                        </Button>
                    </Box>
                
                    <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
                        <Button variant="contained" color="primary" onClick={handleSubmit} disabled={isButtonDisabled}>Register</Button>
                        <Button variant="contained" color="secondary" onClick={handleReset}>Reset</Button>                        
                        <Button variant="outlined" color="secondary" onClick={handleCancel}>Cancel</Button>
                    </Box>
                </StyledInputArea>
            </StyledContainer>
        </>
    )
}

export default EntityRegistrationPage