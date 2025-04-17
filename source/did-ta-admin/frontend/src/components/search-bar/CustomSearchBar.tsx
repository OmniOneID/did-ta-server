import * as React from 'react';
import {
  TextField,
  InputAdornment,
  Button,
  Box,
  Select,
  MenuItem,
  FormControl,
  SelectChangeEvent,
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';

interface CustomSearchBarProps {
  searchText: string;
  setSearchText: (text: string) => void;
  selectedSearch: string;
  setSelectedSearch: (value: string) => void;
  onSearch?: (searchField: string, searchText: string) => void;
  searchOptions?: Array<{ value: string; label: string }>;
}

export default function CustomSearchBar({
  searchText: externalSearchText,
  setSearchText,
  selectedSearch,
  setSelectedSearch,
  onSearch,
  searchOptions = [],
}: CustomSearchBarProps) {
  const inputRef = React.useRef<HTMLInputElement | null>(null);
  const defaultValueRef = React.useRef(externalSearchText);
  const lastSearchedValueRef = React.useRef(externalSearchText);
  const isFirstRender = React.useRef(true);
  
  React.useEffect(() => {
    if (isFirstRender.current) {
      isFirstRender.current = false;
      return;
    }
    
    if (externalSearchText === lastSearchedValueRef.current) {
      return;
    }
    
    if (inputRef.current && document.activeElement !== inputRef.current) {
      inputRef.current.value = externalSearchText;
    }
  }, [externalSearchText]);
  
  const executeSearch = React.useCallback(() => {
    if (!onSearch || !inputRef.current) return;
    
    const currentValue = inputRef.current.value;
    lastSearchedValueRef.current = currentValue;
    
    setSearchText(currentValue);
    onSearch(selectedSearch, currentValue);
  }, [onSearch, selectedSearch, setSearchText]);
  
  const handleSearchClick = React.useCallback(() => {
    executeSearch();
  }, [executeSearch]);
  
  const handleSearchOptionChange = React.useCallback((e: SelectChangeEvent<string>) => {
    setSelectedSearch(e.target.value);
  }, [setSelectedSearch]);
  
  const handleBlur = React.useCallback(() => {
    if (inputRef.current) {
      if (inputRef.current.value !== externalSearchText) {
        setSearchText(inputRef.current.value);
      }
    }
  }, [setSearchText, externalSearchText]);
  
  const handleKeyDown = React.useCallback((e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && onSearch) {
      e.preventDefault();
      executeSearch();
    }
  }, [executeSearch, onSearch]);

  return (
    <Box sx={{ display: 'flex', gap: '6px', alignItems: 'center' }}>
      <FormControl size="small" sx={{ minWidth: 100 }}>
        <Select
          value={selectedSearch}
          onChange={handleSearchOptionChange}
          displayEmpty
          size="small"
        >
          {searchOptions.map((option) => (
            <MenuItem key={option.value} value={option.value}>
              {option.label}
            </MenuItem>
          ))}
        </Select>
      </FormControl>

      <TextField
        inputRef={inputRef}
        size="small"
        variant="outlined"
        placeholder="Enter keyword"
        defaultValue={defaultValueRef.current}
        onBlur={handleBlur}
        onKeyDown={handleKeyDown}
        slotProps={{
          input: {
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon fontSize="small" />
              </InputAdornment>
            ),
          },
        }}
        sx={{ width: 200 }}
      />

      <Button
        variant="contained"
        color="primary"
        onClick={handleSearchClick}
        disabled={!onSearch}
        size="small"
        sx={{ minWidth: 80, height: 36 }}
      >
        Search
      </Button>
    </Box>
  );
}