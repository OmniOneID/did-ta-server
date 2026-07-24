import { Box, Button, Paper, TextField, Typography } from "@mui/material";
import { useDialogs } from "@toolpad/core";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import type { Oid4vciIssuerForm } from "../../../apis/models/Oid4vciIssuer";
import { getOid4vciIssuer, updateOid4vciIssuer } from "../../../apis/list-api";
import CustomConfirmDialog from "../../../components/dialog/CustomConfirmDialog";
import CustomDialog from "../../../components/dialog/CustomDialog";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { formatErrorMessage } from "../../../utils/error-handler";
import { defaultMetadataUri, validateIssuerForm } from "./oid4vciIssuerConfig";

const emptyForm: Oid4vciIssuerForm = {
  credentialIssuer: "",
  credentialIssuerMetadataUri: "",
  userInitiationUri: "",
};

export default function Oid4vciIssuerFormPage() {
  const { id } = useParams();
  const numericId = Number(id);
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState<Oid4vciIssuerForm>(emptyForm);
  const [metadataCustomized, setMetadataCustomized] = useState(false);
  const [errors, setErrors] = useState<Partial<Record<keyof Oid4vciIssuerForm, string>>>({});

  useEffect(() => {
    if (!Number.isFinite(numericId)) {
      navigate("/list-settings/oid4vci-issuers", { replace: true });
      return;
    }
    getOid4vciIssuer(numericId)
      .then(({ data }) => {
        setForm({
          credentialIssuer: data.credentialIssuer,
          credentialIssuerMetadataUri: data.credentialIssuerMetadataUri,
          userInitiationUri: data.userInitiationUri,
        });
        setMetadataCustomized(true);
      })
      .catch((error) => navigate("/error", { state: { message: formatErrorMessage(error, "Failed to load Issuer") } }))
      .finally(() => setLoading(false));
  }, [numericId, navigate]);

  const change = (field: keyof Oid4vciIssuerForm, value: string) => {
    setForm((current) => {
      const next = { ...current, [field]: value };
      if (field === "credentialIssuer" && !metadataCustomized) {
        next.credentialIssuerMetadataUri = value ? defaultMetadataUri(value) : "";
      }
      return next;
    });
    if (field === "credentialIssuerMetadataUri") setMetadataCustomized(true);
    setErrors((current) => ({ ...current, [field]: undefined }));
  };

  const submit = async () => {
    const validation = validateIssuerForm(form);
    setErrors(validation);
    if (Object.keys(validation).length) return;
    const confirmed = await dialogs.open(CustomConfirmDialog, {
      title: "Confirmation",
      message: "Save changes to this Credential Issuer?",
      isModal: true,
    });
    if (!confirmed) return;
    setLoading(true);
    try {
      await updateOid4vciIssuer(numericId, form);
      await dialogs.open(CustomDialog, {
        title: "Notification",
        message: "Credential Issuer updated.",
        isModal: true,
      });
      navigate("/list-settings/oid4vci-issuers");
    } catch (error) {
      await dialogs.open(CustomDialog, {
        title: "Unable to save",
        message: formatErrorMessage(error, "Failed to save Credential Issuer. Check for duplicate registration."),
        isModal: true,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <FullscreenLoader open={loading} />
      <Paper sx={{ maxWidth: 760, mx: "auto", mt: 1, p: 3 }}>
        <Typography variant="h5" fontWeight={700} mb={2}>
          Update Credential Issuer
        </Typography>
        <Box display="grid" gap={2}>
          <TextField label="Credential Issuer *" value={form.credentialIssuer}
            onChange={(e) => change("credentialIssuer", e.target.value)}
            error={!!errors.credentialIssuer} helperText={errors.credentialIssuer} />
          <TextField label="Credential Issuer Metadata URI *" value={form.credentialIssuerMetadataUri}
            onChange={(e) => change("credentialIssuerMetadataUri", e.target.value)}
            error={!!errors.credentialIssuerMetadataUri} helperText={errors.credentialIssuerMetadataUri || "Generated automatically until manually edited."} />
          <TextField label="User Initiation URI *" value={form.userInitiationUri}
            onChange={(e) => change("userInitiationUri", e.target.value)}
            error={!!errors.userInitiationUri} helperText={errors.userInitiationUri} />
          <Box display="flex" justifyContent="flex-end" gap={1}>
            <Button onClick={() => navigate(-1)}>Cancel</Button>
            <Button variant="contained" onClick={submit} disabled={loading}>Save</Button>
          </Box>
        </Box>
      </Paper>
    </>
  );
}
