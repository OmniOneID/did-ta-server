import * as React from 'react';
import { GridToolbarContainer } from '@mui/x-data-grid';
import { Button, Box } from '@mui/material';
import CustomSearchBar from '../search-bar/CustomSearchBar';

interface CustomToolbarProps {
  enableSearch?: boolean;
  searchText: string;
  setSearchText: (text: string) => void;
  selectedSearch: string;
  setSelectedSearch: (value: string) => void;
  onSearch?: (searchField: string, searchText: string) => void;
  onRegister?: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
  disableEdit: boolean;
  disableDelete: boolean;
  searchOptions?: Array<{ value: string; label: string }>;
}

export default function CustomToolbar({
  enableSearch = false,
  searchText,
  setSearchText,
  selectedSearch,
  setSelectedSearch,
  onSearch,
  onRegister,
  onEdit,
  onDelete,
  disableEdit,
  disableDelete,
  searchOptions,
}: CustomToolbarProps) {
  return (
    <GridToolbarContainer sx={{ display: 'flex', alignItems: 'center', padding: '8px' }}>
      {/* 검색바가 있을 경우 왼쪽 정렬 */}
      {enableSearch && (
        <Box sx={{ flex: 1 }}>
          <CustomSearchBar
            searchText={searchText}
            setSearchText={setSearchText}
            selectedSearch={selectedSearch}
            setSelectedSearch={setSelectedSearch}
            onSearch={onSearch}
            searchOptions={searchOptions}
          />
        </Box>
      )}

      {/* 버튼 영역을 항상 우측 정렬 */}
      <Box sx={{ display: 'flex', gap: '4px', justifyContent: 'flex-end', flex: enableSearch ? 1 : 'auto', width: '100%' }}>
        {onRegister && (
          <Button variant="contained" color="primary" onClick={onRegister}>
            등록
          </Button>
        )}
        {onEdit && (
          <Button variant="contained" color="primary" onClick={onEdit} disabled={disableEdit}>
            수정
          </Button>
        )}
        {onDelete && (
          <Button variant="contained" color="error" onClick={onDelete} disabled={disableDelete}>
            삭제
          </Button>
        )}
      </Box>
    </GridToolbarContainer>
  );
}
