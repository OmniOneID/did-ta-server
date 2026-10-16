import {
  Alert, Box, Button, Checkbox, FormControl, FormControlLabel, FormGroup, FormHelperText, FormLabel,
  MenuItem, Paper, TextField, Typography,
} from "@mui/material";
import { useDialogs } from "@toolpad/core";
import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router";
import type { MdocIssuer, MdocIssuerMetadata, MdocIssuerStatus } from "../../../apis/models/MdocIssuer";
import {
  fetchMdocIssuerList, getMdocIssuer, getMdocIssuerMetadata, registerMdocIssuer, updateMdocIssuer,
} from "../../../apis/list-api";
import { getEntitiesByRole } from "../../../apis/entity-api";
import CustomConfirmDialog from "../../../components/dialog/CustomConfirmDialog";
import CustomDialog from "../../../components/dialog/CustomDialog";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { formatErrorMessage } from "../../../utils/error-handler";
import { docTypeLabel } from "./mdocIssuerConfig";

interface EntityOption {
  id: number;
  did: string;
  name: string;
  serverUrl: string;
  /** Status of this Issuer's existing mDoc grant, if any. */
  grantStatus: MdocIssuerStatus | null;
  grantMetadataUri: string | null;
}

interface FormErrors {
  entityId?: string;
  metadataUri?: string;
  docTypes?: string;
}

const WELL_KNOWN_PATH = "/.well-known/openid-credential-issuer";

const isTaken = (entity: EntityOption) => entity.grantStatus === "ACTIVE" || entity.grantStatus === "SUSPENDED";

/** Metadata URL to try first: the URL of a previous grant, else the well-known path on the Issuer server. */
const suggestedMetadataUri = (entity: EntityOption) => {
  if (entity.grantMetadataUri) return entity.grantMetadataUri;
  try {
    return new URL(entity.serverUrl).origin + WELL_KNOWN_PATH;
  } catch {
    return "";
  }
};

const entityLabel = (entity: EntityOption) => {
  const suffix = isTaken(entity) ? " · already registered" : entity.grantStatus === "REVOKED" ? " · revoked, re-register" : "";
  return `${entity.name} · ${entity.did}${suffix}`;
};

const entityHelperText = (entities: EntityOption[]) => {
  if (!entities.length)
    return "No enrolled Issuer yet. An Issuer appears here after it finishes TAS enrollment (status COMPLETED).";
  if (entities.every(isTaken))
    return "Every enrolled Issuer is already registered. To change it, open the Issuer from the list and choose Edit.";
  return "Enrolled Issuers (status COMPLETED). Active or suspended ones are already registered; revoked ones can be re-registered.";
};

const METADATA_HELPER =
  "The Issuer's OID4VCI metadata (filled in from the Issuer server; change it if needed and press Enter). "
  + "Only mDoc docTypes declared there can be granted.";

const serverMessage = (error: unknown, fallback: string) =>
  (error as { message?: string } | null)?.message || fallback;

/**
 * Registers or re-registers an mDoc trusted issuer (route .../new, optional ?entityId=) or edits it (route .../:id/edit).
 * docTypes are chosen only from the mDoc docTypes the issuer declares in its OID4VCI metadata.
 */
export default function MdocIssuerFormPage() {
  const { id } = useParams();
  const [searchParams] = useSearchParams();
  const isNew = id === undefined;
  const numericId = Number(id);
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [loading, setLoading] = useState(true);
  const [entities, setEntities] = useState<EntityOption[]>([]);
  const [entityId, setEntityId] = useState<number | "">("");
  const [issuer, setIssuer] = useState<MdocIssuer | null>(null);
  const [metadataUri, setMetadataUri] = useState("");
  const [autoFilledUri, setAutoFilledUri] = useState("");
  const [metadata, setMetadata] = useState<MdocIssuerMetadata | null>(null);
  const [selected, setSelected] = useState<string[]>([]);
  const [errors, setErrors] = useState<FormErrors>({});

  const selectedEntity = entities.find((entity) => entity.id === entityId) ?? null;

  // Errors are shown inline, so the loader never has to sit on top of a dialog.
  const loadMetadata = useCallback(async (uri: string, current: string[] | null) => {
    if (!uri.trim()) {
      setErrors((prev) => ({ ...prev, metadataUri: "Enter the Issuer metadata URL." }));
      return;
    }
    setLoading(true);
    try {
      const { data } = await getMdocIssuerMetadata(uri.trim());
      const loaded = data as MdocIssuerMetadata;
      setMetadata(loaded);
      setMetadataUri(loaded.metadataUri);
      setSelected(current ? current.filter((docType) => loaded.docTypes.includes(docType)) : loaded.docTypes);
      setErrors((prev) => ({ ...prev, metadataUri: undefined, docTypes: undefined }));
    } catch (error) {
      setMetadata(null);
      setSelected([]);
      setErrors((prev) => ({ ...prev, metadataUri: serverMessage(error, "Failed to load the Issuer metadata.") }));
    } finally {
      setLoading(false);
    }
  }, []);

  /** Selecting an Issuer fills in its metadata URL (unless the admin typed one) and loads its docTypes. */
  const chooseEntity = useCallback((entity: EntityOption, typedUri: string, previousAutoUri: string) => {
    setEntityId(entity.id);
    setErrors((prev) => ({ ...prev, entityId: undefined }));
    const keepTyped = typedUri.trim() !== "" && typedUri !== previousAutoUri;
    const uri = keepTyped ? typedUri : suggestedMetadataUri(entity);
    if (!keepTyped) {
      setMetadataUri(uri);
      setAutoFilledUri(uri);
    }
    if (uri) loadMetadata(uri, null);
  }, [loadMetadata]);

  useEffect(() => {
    if (isNew) {
      Promise.all([getEntitiesByRole("ISSUER"), fetchMdocIssuerList(0, 1000, null, null)])
        .then(([entityResponse, listResponse]) => {
          const grants = new Map((listResponse.data.content as MdocIssuer[]).map((item) => [item.entityId, item]));
          const options = (entityResponse.data as { id: number; did: string; name: string; status: string; serverUrl: string }[])
            .filter((entity) => entity.status === "COMPLETED")
            .map((entity) => ({
              id: entity.id, did: entity.did, name: entity.name, serverUrl: entity.serverUrl,
              grantStatus: grants.get(entity.id)?.status ?? null,
              grantMetadataUri: grants.get(entity.id)?.metadataUri ?? null,
            }));
          setEntities(options);
          setLoading(false);
          // "Re-register" on a revoked Issuer's detail page opens this form with ?entityId=.
          const preselect = options.find((option) => option.id === Number(searchParams.get("entityId")));
          if (preselect && !isTaken(preselect)) chooseEntity(preselect, "", "");
        })
        .catch((error) => navigate("/error", { state: { message: formatErrorMessage(error, "Failed to load Issuers") } }));
      return;
    }
    if (!Number.isFinite(numericId)) {
      navigate("/list-settings/mdoc-issuers", { replace: true });
      return;
    }
    getMdocIssuer(numericId)
      .then(({ data }) => {
        const loaded = data as MdocIssuer;
        setIssuer(loaded);
        setMetadataUri(loaded.metadataUri ?? "");
        setLoading(false);
        if (loaded.metadataUri) loadMetadata(loaded.metadataUri, loaded.docTypes);
      })
      .catch((error) => navigate("/error", { state: { message: formatErrorMessage(error, "Failed to load mDoc Issuer") } }));
  }, [isNew, numericId, navigate, loadMetadata, chooseEntity, searchParams]);

  const changeMetadataUri = (value: string) => {
    setMetadataUri(value);
    if (metadata && value.trim() !== metadata.metadataUri) {
      setMetadata(null);
      setSelected([]);
    }
    setErrors((prev) => ({ ...prev, metadataUri: undefined }));
  };

  const loadTyped = () => loadMetadata(metadataUri, issuer ? issuer.docTypes : null);

  const toggle = (docType: string) => {
    setSelected((prev) => prev.includes(docType) ? prev.filter((item) => item !== docType) : [...prev, docType]);
    setErrors((prev) => ({ ...prev, docTypes: undefined }));
  };

  const reRegistering = isNew && selectedEntity?.grantStatus === "REVOKED";

  const submit = async () => {
    const validation: FormErrors = {
      entityId: isNew && entityId === "" ? "Select an Issuer." : undefined,
      metadataUri: metadata ? undefined : "Load the Issuer metadata first.",
      docTypes: metadata && !selected.length ? "Select at least one docType." : undefined,
    };
    setErrors(validation);
    if (validation.entityId || validation.metadataUri || validation.docTypes || !metadata) return;

    const confirmed = await dialogs.open(CustomConfirmDialog, {
      title: "Confirmation",
      message: reRegistering
        ? "Re-register this revoked Issuer? It becomes Active again and appears in the list mDoc readers download."
        : isNew
          ? "Register this Issuer as an mDoc trusted issuer? It becomes Active and appears in the list mDoc readers download."
          : "Save changes? mDoc readers pick them up on their next refresh.",
      isModal: true,
    });
    if (!confirmed) return;

    // The loader backdrop sits above dialogs, so it must be closed before any dialog opens.
    setLoading(true);
    let savedId: number;
    try {
      if (isNew) {
        const { data } = await registerMdocIssuer({
          entityId: entityId as number, metadataUri: metadata.metadataUri, docTypes: selected,
        });
        savedId = data.id;
      } else {
        await updateMdocIssuer(numericId, metadata.metadataUri, selected);
        savedId = numericId;
      }
    } catch (error) {
      setLoading(false);
      await dialogs.open(CustomDialog, {
        title: "Unable to save",
        message: serverMessage(error, "Failed to save mDoc trusted issuer."),
        isModal: true,
      });
      return;
    }
    setLoading(false);
    await dialogs.open(CustomDialog, {
      title: "Notification",
      message: reRegistering ? "mDoc trusted issuer re-registered." : isNew ? "mDoc trusted issuer registered." : "mDoc trusted issuer updated.",
      isModal: true,
    });
    navigate(`/list-settings/mdoc-issuers/${savedId}`);
  };

  const undeclared = issuer && metadata
    ? issuer.docTypes.filter((docType) => !metadata.docTypes.includes(docType))
    : [];

  return (
    <>
      <FullscreenLoader open={loading} />
      <Paper sx={{ maxWidth: 760, mx: "auto", mt: 1, p: 3 }}>
        <Typography variant="h5" fontWeight={700} mb={2}>
          {isNew ? "Register mDoc Trusted Issuer" : "Edit mDoc Trusted Issuer"}
        </Typography>
        <Box display="grid" gap={2}>
          {isNew ? (
            <TextField select label="Issuer *" value={entityId}
              onChange={(e) => {
                const entity = entities.find((option) => option.id === Number(e.target.value));
                if (entity) chooseEntity(entity, metadataUri, autoFilledUri);
              }}
              error={!!errors.entityId}
              helperText={errors.entityId || entityHelperText(entities)}>
              {entities.map((entity) => (
                <MenuItem key={entity.id} value={entity.id} disabled={isTaken(entity)}>{entityLabel(entity)}</MenuItem>
              ))}
            </TextField>
          ) : (
            <TextField label="Issuer" value={issuer ? `${issuer.name ?? "-"} · ${issuer.did ?? "-"}` : ""} disabled
              helperText="The Issuer of a registration cannot be changed. Register another Issuer separately." />
          )}

          {reRegistering && (
            <Alert severity="info">This Issuer was revoked. Registering it again makes it Active with the docTypes you select.</Alert>
          )}

          <Box display="flex" gap={1} alignItems="flex-start">
            <TextField fullWidth label="Issuer metadata URL *" value={metadataUri}
              onChange={(e) => changeMetadataUri(e.target.value)}
              onKeyDown={(e) => { if (e.key === "Enter") { e.preventDefault(); loadTyped(); } }}
              error={!!errors.metadataUri} helperText={errors.metadataUri || METADATA_HELPER} />
            <Button variant="outlined" sx={{ mt: 1, flexShrink: 0 }} disabled={loading} onClick={loadTyped}>
              Load docTypes
            </Button>
          </Box>

          {!isNew && issuer && !issuer.metadataUri && !metadata && (
            <Alert severity="info">
              This Issuer was registered before metadata checks existed. Enter its metadata URL and load docTypes to save changes.
            </Alert>
          )}

          {metadata && (
            <FormControl error={!!errors.docTypes} component="fieldset">
              <FormLabel component="legend">mDoc docTypes declared by {metadata.credentialIssuer ?? "the Issuer"} *</FormLabel>
              <FormGroup>
                {metadata.docTypes.map((docType) => (
                  <FormControlLabel key={docType} label={docTypeLabel(docType)}
                    control={<Checkbox checked={selected.includes(docType)} onChange={() => toggle(docType)} />} />
                ))}
              </FormGroup>
              <FormHelperText>{errors.docTypes || "Grant only what this Issuer actually issues as mDoc."}</FormHelperText>
            </FormControl>
          )}

          {undeclared.length > 0 && (
            <Alert severity="warning">
              Currently granted but not declared in the metadata, so they will be removed on save: {undeclared.join(", ")}
            </Alert>
          )}

          <Box display="flex" justifyContent="flex-end" gap={1}>
            <Button onClick={() => navigate(-1)}>Cancel</Button>
            <Button variant="contained" onClick={submit} disabled={loading || !metadata}>
              {reRegistering ? "Re-register" : isNew ? "Register" : "Save"}
            </Button>
          </Box>
        </Box>
      </Paper>
    </>
  );
}
