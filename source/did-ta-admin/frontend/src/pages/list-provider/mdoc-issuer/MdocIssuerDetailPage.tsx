import {
  Box, Button, Chip, Dialog, DialogActions, DialogContent, DialogTitle,
  Divider, Paper, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography,
} from "@mui/material";
import { useDialogs } from "@toolpad/core";
import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import type { MdocIssuer, MdocIssuerStatus, MdocIssuerStatusHistory } from "../../../apis/models/MdocIssuer";
import { changeMdocIssuerStatus, getMdocIssuer, getMdocIssuerStatusHistory } from "../../../apis/list-api";
import CustomDialog from "../../../components/dialog/CustomDialog";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { formatErrorMessage } from "../../../utils/error-handler";
import { allowedStatusActions, docTypeLabel, requiresReason, statusActionLabel, statusConfig } from "./mdocIssuerConfig";

const Field = ({ label, value }: { label: string; value?: string | null }) => (
  <Box>
    <Typography variant="caption" color="text.secondary">{label}</Typography>
    <Typography sx={{ overflowWrap: "anywhere" }}>{value || "-"}</Typography>
  </Box>
);

export default function MdocIssuerDetailPage() {
  const id = Number(useParams().id);
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [issuer, setIssuer] = useState<MdocIssuer | null>(null);
  const [history, setHistory] = useState<MdocIssuerStatusHistory[]>([]);
  const [loading, setLoading] = useState(true);
  const [target, setTarget] = useState<MdocIssuerStatus | null>(null);
  const [reason, setReason] = useState("");

  const load = useCallback(async () => {
    if (!Number.isFinite(id)) return navigate("/list-settings/mdoc-issuers", { replace: true });
    setLoading(true);
    try {
      const [issuerResponse, historyResponse] = await Promise.all([
        getMdocIssuer(id), getMdocIssuerStatusHistory(id),
      ]);
      setIssuer(issuerResponse.data);
      setHistory(historyResponse.data);
    } catch (error) {
      navigate("/error", { state: { message: formatErrorMessage(error, "Failed to load mDoc Issuer") } });
    } finally {
      setLoading(false);
    }
  }, [id, navigate]);

  useEffect(() => { load(); }, [load]);

  const applyStatus = async () => {
    if (!target || (requiresReason(target) && !reason.trim())) return;
    setLoading(true);
    try {
      await changeMdocIssuerStatus(id, target, reason.trim() || undefined);
      setTarget(null);
      setReason("");
      await load();
      await dialogs.open(CustomDialog, { title: "Notification", message: "Status changed successfully.", isModal: true });
    } catch (error) {
      setLoading(false);
      await dialogs.open(CustomDialog, { title: "Status change failed", message: formatErrorMessage(error, "The existing status was preserved."), isModal: true });
    }
  };

  if (!issuer) return <FullscreenLoader open={loading} />;
  return (
    <>
      <FullscreenLoader open={loading} />
      <Paper sx={{ maxWidth: 1000, mx: "auto", mt: 1, p: 3 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h5" fontWeight={700}>mDoc Trusted Issuer Detail</Typography>
          <Chip label={statusConfig[issuer.status].label} sx={{ color: statusConfig[issuer.status].color }} />
        </Box>
        <Box display="grid" gridTemplateColumns={{ xs: "1fr", md: "1fr 1fr" }} gap={2} my={3}>
          <Field label="Issuer" value={issuer.name} />
          <Field label="DID" value={issuer.did} />
          <Box sx={{ gridColumn: { md: "1 / span 2" } }}>
            <Field label="Issuer metadata URL" value={issuer.metadataUri} />
          </Box>
          <Box sx={{ gridColumn: { md: "1 / span 2" } }}>
            <Typography variant="caption" color="text.secondary">docTypes</Typography>
            <Box display="flex" gap={1} flexWrap="wrap" mt={0.5}>
              {issuer.docTypes.map((docType) => <Chip key={docType} size="small" label={docTypeLabel(docType)} />)}
            </Box>
          </Box>
          <Field label="Current Status" value={issuer.status} />
          <Field label="Entity ID" value={String(issuer.entityId)} />
          <Field label="Registered At" value={issuer.createdAt} />
          <Field label="Last Updated At" value={issuer.updatedAt} />
        </Box>
        <Box display="flex" gap={1} flexWrap="wrap" mb={3}>
          <Button onClick={() => navigate("/list-settings/mdoc-issuers")}>Back</Button>
          <Button variant="outlined" onClick={() => navigate(`/list-settings/mdoc-issuers/${id}/edit`)}>Edit</Button>
          {issuer.status === "REVOKED" && (
            <Button variant="contained" color="success"
              onClick={() => navigate(`/list-settings/mdoc-issuers/new?entityId=${issuer.entityId}`)}>
              Re-register
            </Button>
          )}
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
          <Typography mb={2}>
            {target === "REVOKED"
              ? "This Issuer will be removed from the list mDoc readers download. To bring it back later, use Re-register."
              : "This changes the trusted issuer list that mDoc readers download. The change is recorded in the status history."}
          </Typography>
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
