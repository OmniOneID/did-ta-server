import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import { deleteAllowedCa, fetchAllowedCaLIst } from '../../../apis/list-api';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

type AllowedCaRow = {
    id: string | number;
    walletId: string;
    caList: string;
    createdAt: string;
    updatedAt: string;
};

const AllowedCaManagementPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<AllowedCaRow[]>([]);
    const [searchText, setSearchText] = useState<string>('');
    const [selectedSearch, setSelectedSearch] = useState<string>('walletId');

    const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
        page: 0,
        pageSize: 10,
    });

    const selectedRowData = useMemo(() => {
        return rows.find(row => row.id === selectedRow) || null;
    }, [rows, selectedRow]);

    const fetchData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchAllowedCaLIst(
                paginationModel.page,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
        } catch (err) {
            console.error("Failed to fetch Allowed CA Lists. ", err);
            navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch Allowed Ca Lists") } });
        } finally {
            setLoading(false);
        }
    }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

    const getData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchAllowedCaLIst(
                0,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
            setPaginationModel((prev) => ({ ...prev, page: 0 }));
        } catch (err) {
            console.error("Failed to fetch Allowed CA Lists. ", err);
            setLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Notification',
                message: formatErrorMessage(err, 'Failed to retrieve Allowed CA Lists'),
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

    const handleDelete = async () => {
        const id = selectedRowData?.id as number;
        if (id) {
          const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to delete Allowed Ca List?',
            isModal: true,
          });

          if (result) {
            setLoading(true);
            deleteAllowedCa(id)
              .then(() => {
                dialogs.open(CustomDialog, {
                  title: 'Notification',
                  message: 'Allowed Ca List delete completed.',
                  isModal: true,
                }, {
                  onClose: async () => {
                    setPaginationModel(prev => ({ ...prev }));
                  },
                });
              })
              .catch((err) => {
                console.error("Failed to delete Allowed Ca Clist. ", err);
                navigate('/error', { state: { message: formatErrorMessage(err, "Failed to delete Allowed Ca List") } });
              })
              .finally(() => setLoading(false));
          }
        }
    };

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
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
            <FullscreenLoader open={loading} />
            <StyledContainer>
              <StyledSubTitle>Allowed CA Management</StyledSubTitle>
              <CustomDataGrid
                  rows={rows}
                  columns={[
                      {
                      field: 'walletId',
                      headerName: "Wallet Identifier",
                      width: 250,
                      renderCell: (params) => (
                          <Link
                          component="button"
                          variant='body2'
                          onClick={() => navigate(`/list-settings/allowed-ca/${params.row.id}`)}
                          sx={{ cursor: 'pointer', color: 'primary.main' }}
                          >
                          {params.value}
                          </Link>),
                      },
                      { field: 'caList', headerName: "Allowed CA List", width: 250,
                          renderCell: (params) => {
                              let devices = [];

                              try {
                                devices = JSON.parse(params.value);
                              } catch (error) {
                                devices = params.value;
                              }

                              return (
                                <div>
                                  {Array.isArray(devices) ? (
                                    devices.map((device, index) => (
                                      <div key={index}>{device}</div>
                                    ))
                                  ) : (
                                    <div>{devices}</div>
                                  )}
                                </div>
                              );
                            },
                      },
                      { field: 'createdAt', headerName: "Registered At", width: 150},
                      { field: 'updatedAt', headerName: "Updated At", width: 150},
                  ]}
                  selectedRow={selectedRow}
                  setSelectedRow={setSelectedRow}
                  onEdit={() => {
                      if (selectedRowData) {
                      navigate(`/list-settings/allowed-ca/allowed-ca-edit/${selectedRowData.id}`);
                      }
                  }}
                  onRegister={() => navigate('/list-settings/allowed-ca/allowed-ca-registration')}
                  onDelete={handleDelete}
                  additionalButtons={[]}
                  paginationMode="server"
                  totalRows={totalRows}
                  paginationModel={paginationModel}
                  setPaginationModel={setPaginationModel}
                  enableSearch={true}
                  searchText={searchText}
                  setSearchText={setSearchText}
                  selectedSearch={selectedSearch}
                  setSelectedSearch={setSelectedSearch}
                  searchOptions={[
                      { value: 'walletId', label: 'Wallet ID' },
                  ]}
                  onSearch={handleSearch}
                  onRefresh={getData}
                  getRowHeight={() => 'auto'}
              />
            </StyledContainer>
        </>
    )
}

export default AllowedCaManagementPage
