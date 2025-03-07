import { Link } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import { fetchAllowedCaLIst } from '../../../apis/list-api';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';

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

    const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
        page: 0,
        pageSize: 10,
    });

    const selectedRowData = useMemo(() => {
        return rows.find(row => row.id === selectedRow) || null;
    }, [rows, selectedRow]);


    const handleDelete = async () => {
        alert("삭제");
    };

    useEffect(() => {
        setLoading(true);
        fetchAllowedCaLIst(paginationModel.page, paginationModel.pageSize, null, null)
          .then((response) => {
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
          })
          .catch((error) => {
            console.error("Failed to retrieve Allowed CA List. ", error);
            navigate('/error', { state: { message: `Failed to retrieve Allowed Ca List: ${error}` } });
          })
          .finally(() => setLoading(false));
    }, []);

    
    return (
        <>
            <FullscreenLoader open={loading} />
            <CustomDataGrid 
                rows={rows} 
                columns={[
                    { 
                    field: 'walletId', 
                    headerName: "Wallet Identifier", 
                    width: 200,
                    renderCell: (params) => (
                        <Link 
                        component="button"
                        variant='body2'
                        onClick={() => navigate(`/vp-policy-management/service-management/${params.row.id}`)}
                        sx={{ cursor: 'pointer', color: 'primary.main' }}
                        >
                        {params.value}
                        </Link>),
                    },
                    { field: 'caList', headerName: "Allowed CA List", width: 200,
                        renderCell: (params) => {
                            let devices = [];
                        
                            try {
                              // JSON 문자열을 배열로 변환
                              devices = JSON.parse(params.value);
                            } catch (error) {
                              // JSON 파싱 실패 시 원래 문자열 그대로 사용
                              devices = params.value;
                            }
                        
                            return (
                              <div>
                                {Array.isArray(devices) ? (
                                  devices.map((device, index) => (
                                    <div key={index}>{device}</div> // 한 줄씩 출력
                                  ))
                                ) : (
                                  <div>{devices}</div> // 단일 문자열일 경우 그대로 출력
                                )}
                              </div>
                            );
                          },
                    },
                    { field: 'createdAt', headerName: "Registered At", width: 100},
                    { field: 'updatedAt', headerName: "Updated At", width: 100},
                ]} 
                selectedRow={selectedRow} 
                setSelectedRow={setSelectedRow}
                onEdit={() => {
                    if (selectedRowData) {
                    navigate(`/vp-policy-management/service-management/service-edit/${selectedRowData.id}`);
                    }
                }}
                onRegister={() => navigate('/list-settings/allowed-ca/allowed-ca-registration')}
                onDelete={handleDelete}
                additionalButtons={[
                
                ]}
                paginationMode="server" 
                totalRows={totalRows} 
                paginationModel={paginationModel} 
                setPaginationModel={setPaginationModel} 
            />
        </>
    )
}

export default AllowedCaManagementPage