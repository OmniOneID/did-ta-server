import React, { useState }from 'react'
import { TextField, Typography, SelectChangeEvent, FormControl, InputLabel, Select, MenuItem, FormHelperText, Button, Backdrop, CircularProgress } from '@mui/material'
import Box from '@mui/material/Box'
import { useNavigate } from 'react-router';
import { useDialogs } from '@toolpad/core/useDialogs';
import { urlRegex, ipRegex } from '../../utils/regex';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import { roles } from '../../constants/roles';
import { registerEntity, verifyEntityNameUnique } from '../../apis/EntityApi';
import { verifyServerUrl } from '../../apis/ServerApi';
import CustomDialog from '../../components/dialog/CustomDialog';
import FullscreenLoader from '../../components/loading/FullscreenLoader';

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

    const handleChange = (field: keyof EntityFormData) => (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | SelectChangeEvent<string>) => {
        setFormData((prev) => ({ ...prev, [field]: event.target.value as string }));
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
    
        try {
            const response = await verifyServerUrl({ serverUrl: formData.serverUrl });
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

        if (!selectedFile) tempErrors.didDoc = 'Please select a DID document file.';
        if (!formData.name) tempErrors.name = 'Please enter a name.';
        if (!formData.role) tempErrors.role = 'Please select a role.';
        if (!formData.serverUrl) tempErrors.serverUrl = 'Please enter the server URL.';

        if (!formData.serverUrl) tempErrors.serverUrl = 'Please enter the server URL.';
        else if (!urlRegex.test(formData.serverUrl) && !ipRegex.test(formData.serverUrl))
            tempErrors.serverUrl = 'Please enter a valid URL.';
        
        if (!tempErrors.name) {
            if (!isNameValid) tempErrors.name = 'Please check for duplicate names.';
        }
        if (!tempErrors.serverUrl) {
            if (!isServerValid) tempErrors.serverUrl = 'Please test the server connection.';
        }
        
        setErrors(tempErrors);
        return Object.keys(tempErrors).length === 0;
    };

    const handleSubmit = async () => {
        if (!validate()) return;
        setIsLoading(true);
        
        const formDataObj = new FormData();
        formDataObj.append('didDoc', selectedFile as File);
        formDataObj.append('name', formData.name || '');
        formDataObj.append('role', formData.role || '');
        formDataObj.append('serverUrl', formData.serverUrl || '');
        formDataObj.append('certificateUrl', formData.serverUrl + '/api/v1/certificate-vc');
        
        try {
            await registerEntity(formDataObj);
            setIsLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Notification',
                message: 'Completed entity registration.',
                isModal: true,
            });
            navigate('/entities/entity-management');
        } catch (error) {
            await dialogs.open(CustomDialog, {
                title: 'Notification',
                message: `Failed to register entity: ${error}`,
                isModal: true,
            });
        } finally {
            setIsLoading(false);
        }
    };

    const handleCancel = async () => {
        const result = await dialogs.open(CustomConfirmDialog, {
          title: 'Confirmation',
          message: 'Are you sure you want to cancel entity registration',
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

    return (
        <>
            <FullscreenLoader open={isLoading} />
            <Box sx={{ p: 3 }}>
                <Typography variant="h4">Entity Registration</Typography>
                    <Box sx={{ maxWidth: 600, margin: 'auto', mt: 2, p: 3, border: '1px solid #ccc', borderRadius: 2 }}>         
                        <FormControl fullWidth margin="normal" error={!!errors.didDoc}>
                            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'left', mb: 1 }}>
                                <Typography variant="body1" sx={{ mr: 5 }}>DID Document: </Typography>

                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, minWidth: 200 }}>
                                    <Button variant="contained" component="label">
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
                                label="Name"
                                variant="outlined"
                                margin="normal"
                                value={formData.name}
                                onChange={handleChange('name')}
                                error={!!errors.name}
                                helperText={errors.name}
                                sx={{minWidth: 250}}
                            />
                            <Button 
                                variant="contained" 
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
                            <InputLabel>Role</InputLabel>
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
                                label="URL"
                                variant="outlined"
                                margin="normal"
                                value={formData.serverUrl}
                                onChange={handleChange('serverUrl')}
                                error={!!errors.serverUrl}
                                helperText={errors.serverUrl}
                                sx={{minWidth: 250}}
                            />
                            <Button 
                                variant="contained" 
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
                            <Button variant="contained" color="secondary" onClick={handleCancel}>Cancel</Button>
                            <Button variant="contained" color="primary" onClick={handleReset}>Reset</Button>
                            <Button variant="contained" color="primary" onClick={handleSubmit} disabled={isButtonDisabled}>Submit</Button>
                        </Box>
                </Box>
            </Box>
           
        </>
    )
}

export default EntityRegistrationPage