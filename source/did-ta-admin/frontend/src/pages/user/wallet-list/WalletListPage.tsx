import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState, useCallback } from 'react';
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { fetchWallets } from '../../../apis/user-api';
import { formatErrorMessage } from '../../../utils/error-handler';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomDialog from '../../../components/dialog/CustomDialog';

type Props = {};

type WalletRow = {
  id: string | number;
  walletId: string;
  did: string;
  status: string;
  registeredAt: string;
  cancelledAt: string;
  createdAt: string;
  updatedAt: string;
};

const WalletListPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [loading, setLoading] = useState(false);
  const [totalRows, setTotalRows] = useState(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [rows, setRows] = useState<WalletRow[]>([]);

  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
    page: 0,
    pageSize: 10,
  });

  const [searchText, setSearchText] = useState('');
  const [selectedSearch, setSelectedSearch] = useState('walletId');

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchWallets(
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
        message: formatErrorMessage(err, 'Failed to fetch Wallet List'),
        isModal: true,
      });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, dialogs]);

  const getData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchWallets(
        0,
        paginationModel.pageSize,
        selectedSearch && searchText.trim() ? selectedSearch : null,
        selectedSearch && searchText.trim() ? searchText.trim() : null
      );
      setRows(response.data.content);
      setTotalRows(response.data.totalElements);
      setPaginationModel((prev) => ({ ...prev, page: 0 }));
    } catch (err) {
      console.error('Failed to fetch Wallet List ', err);
      setLoading(false);
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: formatErrorMessage(err, 'Failed to retrieve Wallet List'),
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
        <StyledSubTitle>Wallet List</StyledSubTitle>
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
                  onClick={() => navigate(`/user-management/wallet-list/${params.row.id}`)}
                  sx={{ cursor: 'pointer', color: 'primary.main' }}
                >
                  {params.value}
                </Link>
              ),
            },
            { field: 'walletId', headerName: 'Wallet ID', width: 200 },
            { field: 'status', headerName: 'Status', width: 200 },
            { field: 'registeredAt', headerName: 'Registered At', width: 150 },
            { field: 'cancelledAt', headerName: 'Cancelled At', width: 150 },
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
            { value: 'walletId', label: 'Wallet ID' },
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

export default WalletListPage;
