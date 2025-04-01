import {
  Box,
  Button,
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  styled,
  Tab,
  Tabs,
  TextField,
  Typography,
  useTheme,
} from '@mui/material';
import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router';
import { getEmailTemplate, registerEmailTemplate } from '../../../apis/noti-api';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
  
  interface EmailTemplateFormData {
    content: string;
  }
  
  interface ErrorState {
    content?: string;
  }
  
  const EmailTemplateSettingsPage = () => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const theme = useTheme();
    const isDarkMode = theme.palette.mode === 'dark';
  
    const [isLoading, setIsLoading] = useState(true);
    const [formData, setFormData] = useState<EmailTemplateFormData>({ content: '' });
    const [errors, setErrors] = useState<ErrorState>({});
    const [tabIndex, setTabIndex] = useState('ISSUE_VC');
    const [originalContent, setOriginalContent] = useState('');
    const [selectedKeyword, setSelectedKeyword] = useState('');
    const inputRef = useRef<HTMLTextAreaElement>(null);
  
    const keywordsMap: Record<string, string[]> = {
      ISSUE_VC: ['{issuerName}', '{qrImg}', '{vcSchemaName}', '{qrExpiredDate}'],
      RESTORE_DID: ['{qrImg}'],
    };
  
    const fetchData = async (templateType: string) => {
      setIsLoading(true);
      try {
        const { data } = await getEmailTemplate('EMAIL', templateType);
        setFormData({ content: data.template });
        setOriginalContent(data.template);
      } catch (error) {
        console.error('데이터를 가져오는 중 오류 발생:', error);
      } finally {
        setIsLoading(false);
      }
    };
  
    useEffect(() => {
      fetchData(tabIndex);
    }, [tabIndex]);
  
    const handleTabChange = (_event: React.SyntheticEvent, newValue: string) => {
      setTabIndex(newValue);
      setSelectedKeyword('');
    };
  
    const handleReset = () => fetchData(tabIndex);
  
    const handleKeywordChange = (event: SelectChangeEvent) => {
      setSelectedKeyword(event.target.value);
    };
  
    const handleInsertKeyword = () => {
      if (!selectedKeyword || !inputRef.current) return;
  
      const textarea = inputRef.current;
      const startPos = textarea.selectionStart;
      const endPos = textarea.selectionEnd;
  
      const newValue =
        formData.content.substring(0, startPos) +
        selectedKeyword +
        formData.content.substring(endPos);
      setFormData({ ...formData, content: newValue });
  
      setTimeout(() => {
        textarea.selectionStart = textarea.selectionEnd = startPos + selectedKeyword.length;
        textarea.focus();
      });
    };
  
    const handleChange = (field: keyof EmailTemplateFormData) => (event: React.ChangeEvent<HTMLInputElement>) => {
      setFormData({ ...formData, [field]: event.target.value });
    };
  
    const validate = (): boolean => {
      const tempErrors: ErrorState = {};
      const keywords = keywordsMap[tabIndex] || [];
  
      if (!formData.content) {
        tempErrors.content = 'Content is required.';
      } else {
        const missingKeywords = keywords.filter((keyword) => !formData.content.includes(keyword));
        if (missingKeywords.length > 0) {
          tempErrors.content = `The following keywords are missing: ${missingKeywords.join(', ')}`;
        }
      }
  
      setErrors(tempErrors);
      return Object.keys(tempErrors).length === 0;
    };
  
    const handleSubmit = async () => {
      if (!validate()) return;
  
      const result = await dialogs.open(CustomConfirmDialog, {
        title: 'Confirmation',
        message: 'Are you sure you want to update the email template?',
        isModal: true,
      });
  
      if (result) {
        setIsLoading(true);
        try {
          const response = await registerEmailTemplate({
            serverType: 'EMAIL',
            templateType: tabIndex,
            template: formData.content,
          });
  
          setOriginalContent(response.data.template);
          dialogs.open(CustomDialog, {
            title: 'Notification',
            message: 'Completed Key Email Server Template registration.',
            isModal: true,
          });
        } catch (error) {
          dialogs.open(CustomDialog, {
            title: 'Notification',
            message: `Failed to register Email Server Template: ${error}`,
            isModal: true,
          });
        } finally {
          setIsLoading(false);
        }
      }
    };

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
      width: 650,
      margin: 'auto',
      marginTop: theme.spacing(1),
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
  
    return (
      <>
        <FullscreenLoader open={isLoading} />
        <StyledContainer>
          <StyledSubTitle>Email Template Settings</StyledSubTitle>

          <Box sx={{ borderBottom: 1, borderColor: 'divider', display: 'flex', justifyContent: 'flex-start' }}>
            <Tabs value={tabIndex} onChange={handleTabChange} variant="scrollable" scrollButtons="auto" sx={{ minWidth: 200 }}>
              <Tab label="VC Issuance" value="ISSUE_VC" />
              <Tab label="DID Restore" value="RESTORE_DID" />
            </Tabs>
          </Box>
  
          <Box sx={{ display: 'flex', alignItems: 'center', mt: 2, gap: 2 }}>
            <FormControl sx={{ minWidth: 200 }} variant="outlined">
              <InputLabel sx={{ backgroundColor: isDarkMode ? '#121212' : 'white', px: 1, color: isDarkMode ? 'white' : 'black' }}>
                Keyword
              </InputLabel>
              <Select
                value={selectedKeyword}
                onChange={handleKeywordChange}
                displayEmpty
                sx={{ backgroundColor: isDarkMode ? '#121212' : 'white', color: isDarkMode ? 'white' : 'black' }}
              >
                {(keywordsMap[tabIndex] || []).map((keyword) => (
                  <MenuItem key={keyword} value={keyword}>
                    {keyword}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <Button variant="contained" onClick={handleInsertKeyword} disabled={!selectedKeyword}>
              Add
            </Button>
          </Box>
  
          <Box sx={{ display: 'flex', flexDirection: 'column' }}>
            <FormControl fullWidth margin="normal" error={!!errors.content}>
              <TextField
                inputRef={inputRef}
                label="Content *"
                variant="filled"
                multiline
                minRows={20}
                value={formData.content ?? ''}
                onChange={handleChange('content')}
                error={!!errors.content}
                sx={{
                  width: '600px',
                  minHeight: '500px',
                  height: '500px',
                  overflow: 'scroll',
                  '& .MuiFilledInput-root': {
                    backgroundColor: isDarkMode ? '#121212' : 'white',
                    color: isDarkMode ? 'white' : 'black',
                    border: '1px solid',
                    borderColor: isDarkMode ? 'white' : 'black',
                    borderRadius: '4px',
                  },
                }}
              />
              {errors.content && <FormHelperText>{errors.content}</FormHelperText>}
            </FormControl>
  
            <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 2 }}>
              <Button variant="contained" color="primary" onClick={handleSubmit} disabled={formData.content === originalContent}>
                Update
              </Button>
              <Button variant="contained" color="secondary" onClick={handleReset}>
                Reset
              </Button>
            </Box>
          </Box>
        </StyledContainer>
      </>
    );
  };
  
  export default EmailTemplateSettingsPage;
  