import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState, useCallback } from 'react';
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { fetchUsers } from '../../../apis/user-api';
import { formatErrorMessage } from '../../../utils/error-handler';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomDialog from '../../../components/dialog/CustomDialog';

type Props = {};

type UserRow = {
  id: string | number;
  did: string;
  status: string;
  pii: string;
  createdAt: string;
  updatedAt: string;
};

const UserListPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [loading, setLoading] = useState(false);
  const [totalRows, setTotalRows] = useState(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [rows, setRows] = useState<UserRow[]>([]);

  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
    page: 0,
    pageSize: 10,
  });

  const [searchText, setSearchText] = useState('');
  const [selectedSearch, setSelectedSearch] = useState('did');

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchUsers(
        paginationModel.page,
        paginationModel.pageSize,
        selectedSearch && searchText.trim() ? selectedSearch : null,
        selectedSearch && searchText.trim() ? searchText.trim() : null
      );
      setRows(response.data.content);
      setTotalRows(response.data.totalElements);
    } catch (err) {
      dialogs.open(CustomDialog, {
        title: 'Notification',
        message: formatErrorMessage(err, 'Failed to fetch User List'),
        isModal: true,
      });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, dialogs]);

  const getData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchUsers(
        0,
        paginationModel.pageSize,
        selectedSearch && searchText.trim() ? selectedSearch : null,
        selectedSearch && searchText.trim() ? searchText.trim() : null
      );
      setRows(response.data.content);
      setTotalRows(response.data.totalElements);
      setPaginationModel((prev) => ({ ...prev, page: 0 }));
    } catch (err) {
      console.error('Failed to fetch User List ', err);
      setLoading(false);
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: formatErrorMessage(err, 'Failed to retrieve User List'),
        isModal: true,
      });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.pageSize, selectedSearch, searchText, dialogs]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleSearch = useCallback(
    (field: string, text: string) => {
      const trimmed = text.trim();
      if (!trimmed) return;

      setSelectedSearch(field);
      setSearchText(trimmed);
      setPaginationModel((prev) => ({ ...prev, page: 0 }));
    },
    []
  );

  const StyledContainer = useMemo(
    () =>
      styled(Box)(({ theme }) => ({
        margin: 'auto',
        marginTop: theme.spacing(1),
        padding: theme.spacing(3),
        border: 'none',
        borderRadius: theme.shape.borderRadius,
        backgroundColor: '#ffffff',
        boxShadow: '0px 4px 8px 0px #0000001A',
      })),
    []
  );

  const StyledSubTitle = useMemo(
    () =>
      styled(Typography)({
        textAlign: 'left',
        fontSize: '24px',
        fontWeight: 700,
      }),
    []
  );

  return (
    <>
      <FullscreenLoader open={loading} />
      <StyledContainer>
        <StyledSubTitle>User List</StyledSubTitle>
        <CustomDataGrid
          rows={rows}
          columns={[
            {
              field: 'did',
              headerName: 'DID',
              width: 250,
              renderCell: (params) => (
                <Link
                  component="button"
                  variant="body2"
                  onClick={() => navigate(`/user-management/user-list/${params.row.id}`)}
                  sx={{ cursor: 'pointer', color: 'primary.main' }}
                >
                  {params.value}
                </Link>
              ),
            },
            { field: 'pii', headerName: 'PII', width: 200 },
            { field: 'createdAt', headerName: 'Registered At', width: 150 },
            { field: 'updatedAt', headerName: 'Updated At', width: 150 },
          ]}
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          paginationMode="server"
          totalRows={totalRows}
          paginationModel={paginationModel}
          setPaginationModel={setPaginationModel}
          enableSearch
          searchOptions={[
            { value: 'did', label: 'DID' },
            { value: 'pii', label: 'PII' }
          ]}
          searchText={searchText}
          setSearchText={setSearchText}
          selectedSearch={selectedSearch}
          setSelectedSearch={setSelectedSearch}
          onSearch={handleSearch}
          onRefresh={getData}
        />
      </StyledContainer>
    </>
  );
};

export default UserListPage;
