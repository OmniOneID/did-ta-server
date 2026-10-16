import { Box, Chip, Link, MenuItem, Select, Tooltip, Typography, styled } from "@mui/material";
import type { GridPaginationModel } from "@mui/x-data-grid";
import { useDialogs } from "@toolpad/core";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router";
import type { MdocIssuer, MdocIssuerStatus } from "../../../apis/models/MdocIssuer";
import { MDOC_ISSUER_STATUSES } from "../../../apis/models/MdocIssuer";
import { fetchMdocIssuerList } from "../../../apis/list-api";
import CustomDataGrid from "../../../components/data-grid/CustomDataGrid";
import CustomDialog from "../../../components/dialog/CustomDialog";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { formatErrorMessage } from "../../../utils/error-handler";
import { statusConfig } from "./mdocIssuerConfig";

export default function MdocIssuerManagementPage() {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState<MdocIssuer[]>([]);
  const [totalRows, setTotalRows] = useState(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [searchText, setSearchText] = useState("");
  const [selectedSearch, setSelectedSearch] = useState("keyword");
  const [status, setStatus] = useState<MdocIssuerStatus | "">("");
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const { data } = await fetchMdocIssuerList(
        paginationModel.page, paginationModel.pageSize, searchText.trim() || null, status || null,
      );
      setRows(data.content);
      setTotalRows(data.totalElements);
    } catch (error) {
      setLoading(false);
      await dialogs.open(CustomDialog, { title: "Unable to load", message: formatErrorMessage(error, "Failed to load mDoc Issuers"), isModal: true });
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
          <Typography fontSize={24} fontWeight={700}>mDoc Trusted Issuers</Typography>
          <Select size="small" displayEmpty value={status} onChange={(e) => { setStatus(e.target.value as MdocIssuerStatus | ""); setPaginationModel(p => ({ ...p, page: 0 })); }}>
            <MenuItem value="">All statuses</MenuItem>
            {MDOC_ISSUER_STATUSES.map((item) => <MenuItem key={item} value={item}>{statusConfig[item].label}</MenuItem>)}
          </Select>
        </Box>
        <CustomDataGrid
          rows={rows}
          columns={[
            { field: "name", headerName: "Issuer", minWidth: 180, flex: 1,
              renderCell: (p) => <Link component="button" onClick={() => navigate(`/list-settings/mdoc-issuers/${p.row.id}`)}>{p.value || "(missing entity)"}</Link> },
            { field: "did", headerName: "DID", minWidth: 200, flex: 1,
              renderCell: (p) => <Tooltip title={p.value || ""}><span style={{ overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{p.value || "-"}</span></Tooltip> },
            { field: "docTypes", headerName: "docTypes", minWidth: 240, flex: 1.2, sortable: false,
              renderCell: (p) => <Tooltip title={(p.value as string[]).join(", ")}><span style={{ overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{(p.value as string[]).join(", ")}</span></Tooltip> },
            { field: "status", headerName: "Status", width: 120, renderCell: (p) => <Chip size="small" label={statusConfig[p.value as MdocIssuerStatus].label} sx={{ color: statusConfig[p.value as MdocIssuerStatus].color }} /> },
            { field: "createdAt", headerName: "Registered At", width: 180 },
            { field: "updatedAt", headerName: "Updated At", width: 180 },
          ]}
          selectedRow={selectedRow} setSelectedRow={setSelectedRow}
          onRegister={() => navigate("/list-settings/mdoc-issuers/new")}
          onEdit={() => selected && navigate(`/list-settings/mdoc-issuers/${selected.id}/edit`)}
          additionalButtons={[]} paginationMode="server" totalRows={totalRows}
          paginationModel={paginationModel} setPaginationModel={setPaginationModel}
          enableSearch searchText={searchText} setSearchText={setSearchText}
          selectedSearch={selectedSearch} setSelectedSearch={setSelectedSearch}
          searchOptions={[{ value: "keyword", label: "DID / Issuer name" }]}
          onSearch={(_, text) => { setSearchText(text.trim()); setPaginationModel(p => ({ ...p, page: 0 })); }}
          onRefresh={load}
        />
      </Container>
    </>
  );
}
