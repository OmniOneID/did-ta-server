import * as React from 'react';
import { DataGrid, GridColDef, GridRowSelectionModel } from '@mui/x-data-grid';
import CustomToolbar from '../tool-bar/CustomToolbar';

interface CustomDataGridProps {
  rows: Array<{ id: string | number } & Record<string, any>>;
  columns: GridColDef[];
  enableSearch?: boolean;
  onSearch?: (searchField: string, searchText: string) => void;
  onRegister?: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
  selectedRow: string | number | null;
  setSelectedRow: (id: string | number | null) => void;
  searchOptions?: Array<{ value: string; label: string }>;
}

export default function CustomDataGrid({
  rows,
  columns,
  enableSearch = false,
  onSearch,
  onRegister,
  onEdit,
  onDelete,
  selectedRow,
  setSelectedRow,
  searchOptions,
}: CustomDataGridProps) {
  const [searchText, setSearchText] = React.useState('');
  const [selectedSearch, setSelectedSearch] = React.useState(searchOptions?.[0]?.value || '');

  return (
    <div style={{ display: 'flex', flexDirection: 'column' }}>
      <DataGrid
        checkboxSelection
        disableMultipleRowSelection
        rows={rows}
        columns={columns}
        pageSizeOptions={[10, 20, 50]}
        disableColumnResize
        density="compact"
        onRowSelectionModelChange={(selectedIds: GridRowSelectionModel) => {
          const selectedId = selectedIds.length > 0 ? selectedIds[0] : null;
          setSelectedRow(selectedId as string | number | null);
        }}
        slots={{
          toolbar: () => (
            <CustomToolbar
              enableSearch={enableSearch}
              searchText={searchText}
              setSearchText={setSearchText}
              selectedSearch={selectedSearch} 
              setSelectedSearch={setSelectedSearch}
              onSearch={onSearch}
              onRegister={onRegister}
              onEdit={onEdit}
              onDelete={onDelete}
              disableEdit={!selectedRow}
              disableDelete={!selectedRow}
              searchOptions={searchOptions}
            />
          ),
        }}
      />
    </div>
  );
}
