import {
  Box, Button, Chip, Dialog, DialogActions, DialogContent, DialogTitle,
  Divider, Paper, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography,
} from "@mui/material";
import { useDialogs } from "@toolpad/core";
import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import type { Oid4vciIssuer, Oid4vciIssuerStatus, Oid4vciStatusHistory } from "../../../apis/models/Oid4vciIssuer";
import { changeOid4vciIssuerStatus, getOid4vciIssuer, getOid4vciIssuerStatusHistory } from "../../../apis/list-api";
import CustomDialog from "../../../components/dialog/CustomDialog";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { formatErrorMessage } from "../../../utils/error-handler";
import { allowedStatusActions, requiresReason, statusActionLabel, statusConfig } from "./oid4vciIssuerConfig";

const Field = ({ label, value }: { label: string; value?: string | null }) => (
  <Box>
    <Typography variant="caption" color="text.secondary">{label}</Typography>
    <Typography sx={{ overflowWrap: "anywhere" }}>{value || "-"}</Typography>
  </Box>
);

export default function Oid4vciIssuerDetailPage() {
  const id = Number(useParams().id);
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [issuer, setIssuer] = useState<Oid4vciIssuer | null>(null);
  const [history, setHistory] = useState<Oid4vciStatusHistory[]>([]);
  const [loading, setLoading] = useState(true);
  const [target, setTarget] = useState<Oid4vciIssuerStatus | null>(null);
  const [reason, setReason] = useState("");

  const load = useCallback(async () => {
    if (!Number.isFinite(id)) return navigate("/list-settings/oid4vci-issuers", { replace: true });
    setLoading(true);
    try {
      const [issuerResponse, historyResponse] = await Promise.all([
        getOid4vciIssuer(id), getOid4vciIssuerStatusHistory(id),
      ]);
      setIssuer(issuerResponse.data);
      setHistory(historyResponse.data);
    } catch (error) {
      navigate("/error", { state: { message: formatErrorMessage(error, "Failed to load Credential Issuer") } });
    } finally {
      setLoading(false);
    }
  }, [id, navigate]);

  useEffect(() => { load(); }, [load]);

  const applyStatus = async () => {
    if (!target || (requiresReason(target) && !reason.trim())) return;
    setLoading(true);
    try {
      await changeOid4vciIssuerStatus(id, target, reason.trim() || undefined);
      setTarget(null);
      setReason("");
      await load();
      await dialogs.open(CustomDialog, { title: "Notification", message: "Status changed successfully.", isModal: true });
    } catch (error) {
      await dialogs.open(CustomDialog, { title: "Status change failed", message: formatErrorMessage(error, "The existing status was preserved."), isModal: true });
      setLoading(false);
    }
  };

  if (!issuer) return <FullscreenLoader open={loading} />;
  return (
    <>
      <FullscreenLoader open={loading} />
      <Paper sx={{ maxWidth: 1000, mx: "auto", mt: 1, p: 3 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h5" fontWeight={700}>Credential Issuer Detail</Typography>
          <Chip label={statusConfig[issuer.status].label} sx={{ color: statusConfig[issuer.status].color }} />
        </Box>
        <Box display="grid" gridTemplateColumns={{ xs: "1fr", md: "1fr 1fr" }} gap={2} my={3}>
          <Field label="Credential Issuer" value={issuer.credentialIssuer} />
          <Field label="Credential Issuer Metadata URI" value={issuer.credentialIssuerMetadataUri} />
          <Field label="User Initiation URI" value={issuer.userInitiationUri} />
          <Field label="Current Status" value={issuer.status} />
          <Field label="Registration Requested At" value={issuer.createdAt} />
          <Field label="Last Updated At" value={issuer.updatedAt} />
        </Box>
        <Box display="flex" gap={1} flexWrap="wrap" mb={3}>
          <Button onClick={() => navigate("/list-settings/oid4vci-issuers")}>Back</Button>
          <Button variant="outlined" onClick={() => navigate(`/list-settings/oid4vci-issuers/${id}/edit`)}>Edit</Button>
          {allowedStatusActions[issuer.status].map((status) => (
            <Button key={status} variant="contained" color={status === "ACTIVE" ? "success" : "warning"} onClick={() => setTarget(status)}>
              {statusActionLabel[status]}
            </Button>
          ))}
        </Box>
        <Divider />
        <Typography variant="h6" fontWeight={700} mt={3}>Status History</Typography>
        <Table size="small">
          <TableHead><TableRow><TableCell>Changed At</TableCell><TableCell>Previous</TableCell><TableCell>New</TableCell><TableCell>Reason</TableCell><TableCell>Changed By</TableCell></TableRow></TableHead>
          <TableBody>
            {history.map((item) => (
              <TableRow key={item.id}><TableCell>{item.changedAt}</TableCell><TableCell>{item.previousStatus || "-"}</TableCell><TableCell>{item.newStatus}</TableCell><TableCell>{item.reason || "-"}</TableCell><TableCell>{item.changedBy || "-"}</TableCell></TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
      <Dialog open={!!target} onClose={() => !loading && setTarget(null)} fullWidth maxWidth="sm">
        <DialogTitle>Confirm status change to {target}</DialogTitle>
        <DialogContent>
          <Typography mb={2}>This operation is audited and may affect Wallet visibility.</Typography>
          {target && requiresReason(target) && (
            <TextField autoFocus fullWidth multiline minRows={3} label="Reason *" value={reason} onChange={(e) => setReason(e.target.value)} inputProps={{ maxLength: 1000 }} />
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setTarget(null)} disabled={loading}>Cancel</Button>
          <Button variant="contained" onClick={applyStatus} disabled={loading || (!!target && requiresReason(target) && !reason.trim())}>Confirm</Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
