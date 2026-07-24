import { Box, Chip, Link, MenuItem, Select, Tooltip, Typography, styled } from "@mui/material";
import type { GridPaginationModel } from "@mui/x-data-grid";
import { useDialogs } from "@toolpad/core";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router";
import type { Oid4vciIssuer, Oid4vciIssuerStatus } from "../../../apis/models/Oid4vciIssuer";
import { OID4VCI_ISSUER_STATUSES } from "../../../apis/models/Oid4vciIssuer";
import { fetchOid4vciIssuerList } from "../../../apis/list-api";
import CustomDataGrid from "../../../components/data-grid/CustomDataGrid";
import CustomDialog from "../../../components/dialog/CustomDialog";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { formatErrorMessage } from "../../../utils/error-handler";
import { statusConfig } from "./oid4vciIssuerConfig";

const UriCell = ({ value }: { value: string }) => (
  <Tooltip title={value}><span style={{ overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{value}</span></Tooltip>
);

export default function Oid4vciIssuerManagementPage() {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState<Oid4vciIssuer[]>([]);
  const [totalRows, setTotalRows] = useState(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [searchText, setSearchText] = useState("");
  const [selectedSearch, setSelectedSearch] = useState("credentialIssuer");
  const [status, setStatus] = useState<Oid4vciIssuerStatus | "">("");
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const { data } = await fetchOid4vciIssuerList(
        paginationModel.page, paginationModel.pageSize, searchText.trim() || null, status || null,
      );
      setRows(data.content);
      setTotalRows(data.totalElements);
    } catch (error) {
      await dialogs.open(CustomDialog, { title: "Unable to load", message: formatErrorMessage(error, "Failed to load Issuers"), isModal: true });
    } finally {
      setLoading(false);
    }
  }, [paginationModel, searchText, status, dialogs]);

  useEffect(() => { load(); }, [load]);
  const selected = rows.find((row) => row.id === selectedRow);
  const Container = useMemo(() => styled(Box)(({ theme }) => ({
    margin: "auto", marginTop: theme.spacing(1), padding: theme.spacing(3),
    borderRadius: theme.shape.borderRadius, backgroundColor: "#fff", boxShadow: "0 4px 8px #0000001A",
  })), []);

  return (
    <>
      <FullscreenLoader open={loading} />
      <Container>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
          <Typography fontSize={24} fontWeight={700}>OID4VCI Credential Issuers</Typography>
          <Select size="small" displayEmpty value={status} onChange={(e) => { setStatus(e.target.value as Oid4vciIssuerStatus | ""); setPaginationModel(p => ({ ...p, page: 0 })); }}>
            <MenuItem value="">All statuses</MenuItem>
            {OID4VCI_ISSUER_STATUSES.map((item) => <MenuItem key={item} value={item}>{statusConfig[item].label}</MenuItem>)}
          </Select>
        </Box>
        <CustomDataGrid
          rows={rows}
          columns={[
            { field: "credentialIssuer", headerName: "Credential Issuer", minWidth: 240, flex: 1,
              renderCell: (p) => <Tooltip title={p.value}><Link component="button" onClick={() => navigate(`/list-settings/oid4vci-issuers/${p.row.id}`)}>{p.value}</Link></Tooltip> },
            { field: "credentialIssuerMetadataUri", headerName: "Issuer Metadata URI", minWidth: 220, flex: 1, renderCell: (p) => <UriCell value={p.value} /> },
            { field: "userInitiationUri", headerName: "User Initiation URI", minWidth: 220, flex: 1, renderCell: (p) => <UriCell value={p.value} /> },
            { field: "status", headerName: "Status", width: 120, renderCell: (p) => <Chip size="small" label={statusConfig[p.value as Oid4vciIssuerStatus].label} sx={{ color: statusConfig[p.value as Oid4vciIssuerStatus].color }} /> },
            { field: "createdAt", headerName: "Requested At", width: 180 },
            { field: "updatedAt", headerName: "Updated At", width: 180 },
          ]}
          selectedRow={selectedRow} setSelectedRow={setSelectedRow}
          onEdit={() => selected && navigate(`/list-settings/oid4vci-issuers/${selected.id}/edit`)}
          additionalButtons={[]} paginationMode="server" totalRows={totalRows}
          paginationModel={paginationModel} setPaginationModel={setPaginationModel}
          enableSearch searchText={searchText} setSearchText={setSearchText}
          selectedSearch={selectedSearch} setSelectedSearch={setSelectedSearch}
          searchOptions={[{ value: "credentialIssuer", label: "Credential Issuer" }]}
          onSearch={(_, text) => { setSearchText(text.trim()); setPaginationModel(p => ({ ...p, page: 0 })); }}
          onRefresh={load}
        />
      </Container>
    </>
  );
}
